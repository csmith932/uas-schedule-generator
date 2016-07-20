/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.datalayer;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.datalayer.identity.*;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.db.DataAccessObject;
import gov.faa.ang.swac.datalayer.storage.fileio.FileMarshaller;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * MappedDataAccess is a client-facing facade for the data layer, acting as a
 * simple key-value store. DataDescriptor keys provide metadata that
 * MappedDataAccess interprets to differentiate between named files,
 * parameterized queries, and transient or persistent intermediate data. A
 * DataMarshaller for loading/saving the data is created and cached for future
 * queries. Data may or may not continue to reside in memory after it is
 * marshalled to/from a data store: that is the marshaller's responsibility.
 * Manual clean-up of references is made possible with purge and purge-all.
 *
 * @author csmith
 *
 */
public class MappedDataAccess extends ResourceManager {

    private static final Logger logger = LogManager.getLogger(MappedDataAccess.class);
    private final Map<DataDescriptor, DataMarshaller> storageMap;
    private final Map<DataDescriptor, List<String>> resourceNameMap;

    public MappedDataAccess() {
        storageMap = new LinkedHashMap<DataDescriptor, DataMarshaller>();
        resourceNameMap = new ConcurrentSkipListMap<DataDescriptor, List<String>>(new DataDescriptorComparator());
    }

    public MappedDataAccess(MappedDataAccess val) {
        super(val);
        this.storageMap = val.storageMap;
        this.resourceNameMap = val.resourceNameMap;
    }

    public void loadScenarioFileDataDescriptor(FileDataDescriptor fdd) {
        if (fdd.getResourceName() == null || fdd.getResourceName().isEmpty()) {
            logger.error("Error loading scenario import for class: " + fdd.getDataType());
        } else {
            List<String> files = new ArrayList<String>();
            StringBuilder parameters = new StringBuilder("");
            
            parameters.append("," + (fdd.getBaseDate() == null ? " " : Long.toString(fdd.getBaseDate().getTime())));
            parameters.append("," + (fdd.getForecastFiscalYear() == null ? " " : fdd.getForecastFiscalYear()));
            parameters.append("," + (fdd.getClassifier() == null ? " " : fdd.getClassifier()));
            
            if (fdd instanceof FileSetDescriptor) {
                for (String file : ((FileSetDescriptor)fdd).getResourceNames()) {
                    files.add(file + parameters);
                }
            } else {
                files.add(fdd.getResourceName() + parameters);
            }
            this.resourceNameMap.put(fdd, files);
            this.storageMap.put(fdd, fdd.createMarshaller(this));
        }
    }

    // TODO: temporary workaround for fuzzy-matching filter criteria on parameterized queries.
    public <T> void load(DataDescriptor descriptor, List<T> output) throws DataAccessException {
        logger.info("Loading data: " + descriptor.toString());
        DataMarshaller marshaller = this.getMarshaller(descriptor);
        if (marshaller == null) {
            marshaller = this.getMarshaller(descriptor);
        }

        marshaller.load(output);
        logger.debug(output.size() + " records of type " + descriptor.getDataType().getSimpleName() + " loaded");
        if (output.isEmpty()) {
            logger.warn("No records loaded for type " + descriptor.getDataType().getSimpleName() + ": data source may be missing or corrupted.");
        }
    }

    public <T> void save(DataDescriptor descriptor, List<T> data) throws DataAccessException {
        DataMarshaller marshaller = this.getMarshaller(descriptor);

        marshaller.save(data);
        logger.debug(data.size() + " records of type " + descriptor.getDataType().getSimpleName() + " saved");
    }

    /**
     * In order to avoid changing object state in a predicate, exists will
     * return false if the descriptor is not already registered in the map XXX:
     * No longer true...getMarshaller will create the marshaller if it does not
     * exist. Existence check should work ok but it potentially changes the
     * object which is bad juju
     *
     * @param descriptor
     * @return
     */
    public boolean exists(DataDescriptor descriptor) throws DataAccessException {
        DataMarshaller marshaller = this.getMarshaller(descriptor);
        return (marshaller != null && marshaller.exists());
    }

    /**
     * This is for free-form text output that circumvents the normal formalism
     * of the key-value store, which is useful for dumping unstructured output
     * data and reports. Use TextSerializable and load/save instead for
     * structured data.
     *
     * @param descriptor
     * @return
     * @throws DataAccessException
     */
    public PrintWriter getWriter(FileDataDescriptor descriptor) throws DataAccessException {
        if (descriptor.isReadOnly()) {
            throw new DataAccessException("Read-only");
        }
        return new PrintWriter(this.openOutput(descriptor.getLocation(), descriptor.getResourceName()));
    }

    /**
     * Resolves a DataDescriptor directly to a file system path. Only
     * FileDataDescriptors are permitted. WARNING: This should be used only as a
     * last resort for pathological cases, since it violates the abstraction
     * provided by the data layer.
     *
     * TODO: Any calling code should be refactored to use data layer abstraction
     * at the earliest convenience.
     *
     * @param descriptor
     * @return
     */
    public File getAbsoluteFile(DataDescriptor descriptor) {
        if (descriptor instanceof FileDataDescriptor) {
            FileDataDescriptor f = (FileDataDescriptor) descriptor;

            if (f.getResourceName() == null || f.getResourceName().isEmpty()) {
                String[] params = this.resourceNameMap.get(f).get(0).split(",");
                f.setResourceName(params[0]);
            }

            return this.getFile(f.getLocation(), f.getResourceName());
        } else {
            throw new IllegalArgumentException();
        }
    }

    public DataMarshaller getMarshaller(DataDescriptor descriptor) throws DataAccessException {
        DataMarshaller marshaller;
        DataDescriptor d = descriptor;

        if (d instanceof FileDataDescriptor) {
            FileDataDescriptor f = (FileDataDescriptor) d;

            FileDataDescriptor key = null;
            boolean requireSuccessfulLookup = false;
            if (f.getResourceName() == null || f.getResourceName().isEmpty()) {
            	key = f;
            	requireSuccessfulLookup = true;
            } else { 
            	key = new FileDataDescriptor(f);
            	key.setLocation(f.getLocation());
            	key.setResourceName(null);
            }
            
            List<String> paramList = this.resourceNameMap.get(key);
            if(!requireSuccessfulLookup)
            	paramList = null;
            if (paramList == null || paramList.isEmpty()) {
            	if (requireSuccessfulLookup) {
            		throw new DataAccessException("Invalid baseDate|forecastFiscalYear entry in scenarioImports for resource type " + f.getDataType().getSimpleName());
            	}
            } else { 
                List<String> files = new ArrayList<String>();
                f = new FileSetDescriptor(f);

                for (String param : paramList) {
                    files.add(param.split(",")[0]);
                }

                ((FileSetDescriptor)f).setResourceNames(files);

                String[] params = paramList.get(0).split(",");

                f.setResourceName(params[0]);
                if (params.length > 1) {
                    if (params[1].trim().length() > 0) {
                        ((FileDataDescriptor)d).setBaseDate(new Timestamp(Long.parseLong(params[1]))); // changes to f are not propogated back to the caller
                        f.setBaseDate(new Timestamp(Long.parseLong(params[1])));
                    }
                    if (params.length > 2 ) {
                        if (params[2].trim().length() > 0) {
                            ((FileDataDescriptor)d).setForecastFiscalYear(Integer.parseInt(params[2])); // changes to f are not propogated back to the caller
                            f.setForecastFiscalYear(Integer.parseInt(params[2]));
                        }
                        if (params.length > 3) {
                            if (params[3].trim().length() > 0) {
                                ((FileDataDescriptor)d).setClassifier(params[3]); // changes to f are not propogated back to the caller
                                f.setClassifier(params[3]);
                            }
                        }
                    }
                }
                String schema = f.getSchemaName();
                
                if (schema != null && !schema.isEmpty()) {
                    storageMap.put(f, f.createMarshaller(this));
                }
                
                d = f;
            }
        }

        marshaller = this.storageMap.get(d);

        if (marshaller == null) {
            marshaller = d.createMarshaller(this);
            this.storageMap.put(d, marshaller);
            logger.debug("Registered metadata for: " + d.toString());
        }

        return marshaller;
    }

    /**
     * Strong typing
     *
     * @param descriptor
     * @return
     * @throws DataAccessException
     */
    public DataAccessObject getDataAccessObject(DataAccessObjectDescriptor descriptor) throws DataAccessException {
        return (DataAccessObject) this.getMarshaller(descriptor);
    }

    // XXX: It's probably cleaner to re-implement the equals method in ParameterizedDataDescriptor to fuzzy-match instead of checking the map for all permutations of parameters
    // NOTE: An important distinction between this and getMarshaller is that this should not insert anything into the storage maps since it is searching in potentially multiple locations
    // getMarshaller works in a get-or-create fashion.
    private DataMarshaller getParameterizedMarshaller(ParameterizedDataDescriptor originalDescriptor) {
        Integer forecastFiscalYearParam = originalDescriptor.getForecastFiscalYear();
        String classifierParam = originalDescriptor.getClassifier();

        ParameterizedDataDescriptor descriptor = originalDescriptor.clone();

        if (!this.storageMap.containsKey(descriptor)) // 111
        {
            descriptor.setClassifier(null); // 110
            if (!this.storageMap.containsKey(descriptor)) {
                descriptor.setForecastFiscalYear(null); // 100
                if (!this.storageMap.containsKey(descriptor)) {
                    descriptor.setClassifier(classifierParam); // 101
                    if (!this.storageMap.containsKey(descriptor)) {
                        descriptor.setBaseDate(null); // 001
                        if (!this.storageMap.containsKey(descriptor)) {
                            descriptor.setForecastFiscalYear(forecastFiscalYearParam); // 011
                            if (!this.storageMap.containsKey(descriptor)) {
                                descriptor.setClassifier(null); // 010
                            }
                        }
                    }
                }
            }
        }
        return this.storageMap.get(descriptor);
    }

    /**
     * The map must be cleared for GC
     */
    public void clearMap() {
        this.storageMap.clear();
        //HK: have to keep resourceNameMap around forever because DB is now initialized in Job so it must
        //look up the location of scripts at it's Thread execution time.
        //this.resourceNameMap.clear();
    }

    @Override
    public String toString() {
        Comparator<Entry<DataDescriptor, DataMarshaller>> comp = new Comparator<Entry<DataDescriptor, DataMarshaller>>() {
            @Override
            public int compare(Entry<DataDescriptor, DataMarshaller> o1,
                    Entry<DataDescriptor, DataMarshaller> o2) {
                if (o1.getClass().equals(o2.getClass())) {
                    if (o1 instanceof IntermediateDataDescriptor) {
                        IntermediateDataDescriptor _o1 = (IntermediateDataDescriptor) o1;
                        IntermediateDataDescriptor _o2 = (IntermediateDataDescriptor) o2;
                        Integer instance1 = _o1.getInstanceId();
                        Integer instance2 = _o2.getInstanceId();
                        if (instance1.equals(instance2)) {
                            String source1 = _o1.getDataSource().toString();
                            String source2 = _o2.getDataSource().toString();
                            if (source1.equals(source2)) {
                                return _o1.getDataType().getSimpleName().compareTo(_o2.getDataType().getSimpleName());
                            } else {
                                return source1.compareTo(source2);
                            }
                        } else {
                            return instance1.compareTo(instance2);
                        }
                    } else {
                        return o1.toString().compareTo(o2.toString());
                    }
                } else {
                    return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
                }
            }
        };

        List<Entry<DataDescriptor, DataMarshaller>> entries = new ArrayList<Entry<DataDescriptor, DataMarshaller>>(this.storageMap.entrySet());
        Collections.sort(entries, comp);

        StringBuilder str = new StringBuilder();
        for (Entry<DataDescriptor, DataMarshaller> entry : entries) {
            str.append(entry.toString() + "\n");
        }
        return str.toString();
    }

    private class DataDescriptorComparator implements Comparator<DataDescriptor> {

        @Override
        public int compare(DataDescriptor o1, DataDescriptor o2) {
            if (o1 instanceof ParameterizedDataDescriptor && o2 instanceof ParameterizedDataDescriptor) {
                if (o1 instanceof FileDataDescriptor && o2 instanceof FileDataDescriptor) {
                    FileDataDescriptor fdd1 = (FileDataDescriptor) o1;
                    FileDataDescriptor fdd2 = (FileDataDescriptor) o2;

                    if (fdd1.equals(fdd2)) {
                        return 0;
                    }
                }
                ParameterizedDataDescriptor pdd1 = (ParameterizedDataDescriptor) o1;
                ParameterizedDataDescriptor pdd2 = (ParameterizedDataDescriptor) o2;

                if (pdd1.getDataType() != pdd2.getDataType()) {
                    return pdd1.getDataType().getCanonicalName().compareTo(pdd2.getDataType().getCanonicalName());
                } else {
                    String c1 = pdd1.getClassifier();
                    String c2 = pdd2.getClassifier();

                    if (c1 == null || c1.isEmpty() || c2 == null || c2.isEmpty() || c1.contentEquals(c2)) {
                        Timestamp ts1 = pdd1.getBaseDate();
                        Timestamp ts2 = pdd2.getBaseDate();

                        if (ts1 == null || ts2 == null || ts1.equalValue(ts2)) {
                            Integer ffy1 = pdd1.getForecastFiscalYear();
                            Integer ffy2 = pdd2.getForecastFiscalYear();

                            if (ffy1 == null || ffy2 == null) {
                                return 0;
                            } else {
                                return ffy1.compareTo(ffy2);
                            }
                        } else {
                            return pdd1.getBaseDate().compareTo(pdd2.getBaseDate());
                        }
                    } else {
                        return pdd1.getClassifier().compareTo(pdd2.getClassifier());
                    }
                }
            }
            return -1;
        }
    }
}
