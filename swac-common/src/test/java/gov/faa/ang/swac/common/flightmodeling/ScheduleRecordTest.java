/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.faa.ang.swac.common.flightmodeling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author chall
 */
public class ScheduleRecordTest 
{   

    @Ignore
	@Test
    public void test() throws IOException 
    {
        String sysLineSep = 
            System.getProperty("line.separator");
        String input = 
            "20926200,20091005,AMF262,91044,ORIGINAL_FLIGHT,20091006 23:49:24,COMPUTED,20091006 23:58:00,FILED,20091007 00:49:00,FILED,20091007 00:53:18,70.0,169.0,SMX,FAT,34.898916666666665,-120.45744444444445,261,1,36.77619444444444,-119.71813888888889,336,1,PA31,P,F,2,-1,KSMX,KFAT,D Cargo,PA31,ATOP,N,,,2094/7228 2115/7246 2140/7237 2169/7240 2203/7247 2206/7230 2209/7211 2213/7189 2207/7183"+ sysLineSep+
            "21536800,20091005,BOE703,57379,ORIGINAL_FLIGHT,20091005 14:50:12,COMPUTED,20091005 15:00:00,FILED,20091006 10:39:00,FILED,20091006 10:44:12,410.0,450.0,BFI,PAE,47.53,-122.30197222222222,21,1,47.90634166666667,-122.28156388888888,606,1,B738,J,O,2,10,KBFI,KPAE,D Other Miscellaneous,B738,ATOP,N,,,2852/7338 2846/7339 2829/7405 2675/7444 2817/7449 2898/7478 2882/7371 2889/7338 2874/7337"+ sysLineSep+
            "21539200,20091005,BOXCR10,90721,ORIGINAL_FLIGHT,20091006 23:49:36,COMPUTED,20091007 00:01:00,FILED,20091007 01:31:00,FILED,20091007 01:36:12,90.0,279.0,WRB,WRB,32.640143333333334,-83.59184888888889,294,1,32.640143333333334,-83.59184888888889,294,1,C27,-,M,2,-1,KWRB,KWRB,D Military,,,N,,,1958/5016 2496/5435 1933/5010 1947/5006 1953/5011 1958/5016"+ sysLineSep+
            "24963800,20091005,SUNY791,37239,ORIGINAL_FLIGHT,20091007 00:18:36,COMPUTED,20091007 00:30:00,FILED,20091007 01:35:00,FILED,20091007 01:39:54,160.0,264.0,NYG,NCA,38.50183333333333,-77.30533333333334,11,1,34.70733611111111,-77.44516388888889,26,1,BE20,T,M,2,10,KNYG,KNCA,D Military,BE20,ATOP,N,,,2310/4638 2300/4641 2293/4630 2285/4621 2278/4611 2250/4639 2104/4623 2083/4646"+ sysLineSep+
            "24555400,20091005,RCH9192,72681,ORIGINAL_FLIGHT,20091006 23:04:35,COMPUTED,20091006 23:15:59,FILED,20091007 07:19:00,FILED,20091007 07:23:54,330.0,430.0,WRI,ETAR,40.0155,-74.59366666666666,131,1,49.436911,7.600283,776,64,C17,J,M,2,10,KWRI,ETAR,D Military,B764,ATOP,N,,,2401/4476 2394/4413 2407/4397 2415/4387 2419/4382 2425/4376 2432/4368 2437/4361 2439/4359 2441/4356 2444/4353 2455/4339 2466/4327 2477/4315 2503/4286 2542/4259 2605/4237 2666/4193 2807/4086 2822/4068 2842/4038 3180/3440 3232/3298 3300/3000 3420/2400 3420/1800 3138/0002 2966/-0456"+ sysLineSep+           
            "-40910,20091006,V_KFTY_13,13,VFR,20091006 18:23:21,CREATED,20091006 18:34:45,CREATED,,,,,,FTY,,33.77913888888889,-84.5213611111111,841,1,,,,,,-,G,,10,KFTY,,D VFR,,,N,,,"+ sysLineSep+
            "-40911,20091006,V_KFTY_14,14,VFR,,,,,20091006 16:35:05,CREATED,20091006 16:40:17,,,,FTY,,,,,33.77913888888889,-84.5213611111111,841,1,,-,G,,10,,KFTY,D VFR,,,N,,,"+ sysLineSep;  
   
        List<ScheduleRecord> schedRecList = new ArrayList<ScheduleRecord>();
        
        // Load the records
        StringReader sr = new StringReader(input);
        BufferedReader br = new BufferedReader(sr);
        try 
        {
            while (br.ready())
            {
                ScheduleRecord schedRec = new ScheduleRecord();
                schedRec.readItem(br);
                schedRecList.add(schedRec);
            }
        }
        catch (NullPointerException ex)
        {
            // This	represents routine termination of file reading
        }     

        // Save the records
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        for (ScheduleRecord schedRec : schedRecList)
        {
            schedRec.writeItem(pw);
        }
        String output = sw.toString();
        
        // Test for equality
        if (!output.equals(input))
        {
            System.out.println("ScheduleRecord test failure:");
            if (output.length() != input.length())
            {
                System.out.println("Lengths: "+input.length()+" != "+output.length());
            }
            System.out.print(input);
            System.out.print(output);
        }
        Assert.assertTrue(output.equals(input));
        
    }
}
