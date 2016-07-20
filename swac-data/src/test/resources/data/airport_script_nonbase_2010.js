function getAirportState(airport, ceiling, visibility, winddir, windspeed, cnd)
{
	var state = null;
	
	switch (airport)
	{
		case "ABQ":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "ANC":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "ATL":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3600) && (visibility >= 7))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "AUS":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3500) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "BDL":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 7))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "BHM":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2400) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "BNA":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2600) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "BOS":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2500) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "BUF":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2300) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "BUR":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3500) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "BWI":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2500) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "CLE":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2600) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "CLT":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3600) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "CVG":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2900) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "DAL":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2400) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "DAY":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2800) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "DCA":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 4))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "DEN":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "DFW":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3500) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "DTW":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "EWR":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 4))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "FLL":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 4000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "GYY":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 1000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "HNL":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2500) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "HOU":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2100) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "HPN":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3500) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "IAD":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 7))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "IAH":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 4000) && (visibility >= 8))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "IND":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2200) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "ISP":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2500) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "JAX":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2100) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "JFK":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2000) && (visibility >= 4))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "LAS":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 5000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "LAX":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2500) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "LGA":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3200) && (visibility >= 4))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "LGB":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2100) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "MCI":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "MCO":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2500) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "MDW":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 1900) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "MEM":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 5000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "MHT":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2500) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "MIA":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "MKE":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2300) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "MSP":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3500) && (visibility >= 8))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "MSY":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "OAK":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2500) && (visibility >= 8))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "OMA":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2500) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "ONT":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "ORD":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 1900) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "OXR":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 1000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "PBI":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "PDX":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3500) && (visibility >= 8))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "PHL":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2300) && (visibility >= 4))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "PHX":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3300) && (visibility >= 7))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "PIT":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 1800) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "PVD":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "RDU":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 4000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "RFD":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 1000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "RSW":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2100) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "SAN":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "SAT":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "SDF":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "SEA":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 4000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "SFO":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3500) && (visibility >= 8))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "SJC":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2500) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "SLC":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 5300) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "SNA":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "STL":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 5000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "SWF":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 1000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "TEB":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3500) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "TPA":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 2100) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "TUS":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 7500) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "VNY":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 1000) && (visibility >= 3))
				state = "VMC";
			else
				state = "MMC";
			break;

		case "ALB":
		case "BOI":
		case "BFL":
		case "BTR":
		case "CHS":
		case "CMH":
		case "COS":
		case "CRP":
		case "DAB":
		case "DSM":
		case "ELP":
		case "EUG":
		case "FAT":
		case "FNT":
		case "FXE":
		case "GFK":
		case "GRR":
		case "GSO":
		case "ICT":
		case "JNU":
		case "LAN":
		case "LIT":
		case "MLB":
		case "MSN":
		case "ORF":
		case "OKC":
		case "PHF":
		case "PIE":
		case "RIC":
		case "RNO":
		case "ROC":
		case "SBA":
		case "SMF":
		case "SYR":
		case "TUL":
		case "TVC":
		case "TYS":
			if ( (ceiling < 1000) || (visibility < 3))
				state = "IMC";
			else if ( (ceiling >= 3000) && (visibility >= 5))
				state = "VMC";
			else
				state = "MMC";
			break;
	}
	
	//state = airport + "_" + state + "_BASE";
	return state;
}
