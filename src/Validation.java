
import java.math.BigDecimal;

/*
 * 
 * Validation.java supports CHECK in the DDL operation 
 * and then later in stored functions. 
 * 
 * @author: Theodore
 * */
public class Validation {
	
	public static final BigDecimal ZERO = BigDecimal.ZERO;
	

	/**
     * Parse single line of buffer input into array by one or more than one separated spaces
     * e.g. AC-123456-a1 macbookPro15   "15'' retina , powered by 3.5GHz dual-core,"
     * @param line
     * @return string res
     */
    public static String[] parseProductxt(String line) {
    	String delims = "[ ]+";
    	String[] res = line.split(delims);
    	return res;
    };
    
    /**
     *  Determine whether parsed data is valid sku
     *  definition of sku: ( the SKU is a 12-character value of the form AA-NNNNNN-CC where A 
     *  is an upper-case letter, N is a digit from 0-9, and C is either a digit or an uppper 
     *  case letter. For example, "AB-123456-0N")
     *  reference: Bibilo.java(P.Gust) 
     *  @param String sku_str 
     */
    public static boolean isSKU(String pku_str)  {
    	// alpha-numerical pattern
//    	String pattern= "^[a-zA-Z0-9]*$";
    	String sku_pattern = "(^[A-Z]{2})-(\\d{6})-([A-Z0-9]{2})$";
    	return pku_str.matches(sku_pattern) && pku_str.length() == 12;
    };
    
    /**
     * 	Determine whether parsed data string is valid price(rounded)
     * 	definition of rounded price: 
     * 	@param priceStr input prices as string.
     *  @return true if input string is proper representation of decimal two digit rounded double 
     */
    public static boolean isRounded(String priceStr){
    	String round_pattern = "(\\d*).(\\d{2})$";
    	
    	return priceStr.matches(round_pattern);
    }
    
    
    
    /**
     * Function transform string into Decimal
     * @param  price_str input price string
     * @return Double formated non-negative double of the input price string.
     */
     
    public static Double strToDecimal(String price_str) {
    	
    	try {
    		if(Double.parseDouble(price_str) >= 0){
    			return Double.parseDouble(price_str);
    		} 
    		System.err.printf("invalid negative price %s", price_str);
    		return -1.00;
    		 	
    	} catch( Exception e ) {
	        return -1.00;
	    }
    }
    

     /**
      * Function determine whether string represents a proper product count
      * @param str String format of Integer 
      * @return  boolean expression for determining qualification of a string  representation of the integer
      *
     */
    public static boolean isProperCount(String str) {
    	 try {
    	        if (Integer.parseInt(str) > 0)return true;
    	        return false;
    	    }
    	    catch( Exception e ) {
    	        return false;
    	    }
    }
    
    /**
     * Determine whether customer-id is valid
     * definition: use valid email address for customerID.
     * @param input of customer_id as email address.
     * @return false if input email address is invalid form true otherwise
     */
    public static boolean isCustomerIDEmailAddress(String id_str) {
    	// use email address as valid id.
    	// pattern src: https://emailregex.com/
    	String pattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    	return id_str.matches(pattern);
    	
    } 
    
    
    /**
     *  Determine whether zip code is validCode
     *  A valid US ZIP code recognizes both the five-digit and nine-digit (ZIP + 4)
     *  @param input postal code string 
     *  @return boolean expression after evaluating the input ZIP code.
	 *	e.g. a valid postal code should match 12345 and 12345-6789, but not 1234, 123456, 123456789, or 1234-56789.
     */
    public static boolean isZipCode(String zip_str) {
    	String pattern = "^[0-9]{5}(?:-[0-9]{4})?$";
    	return zip_str.matches(pattern);
    }

 /**
  *  test "isSKU","isRounded" for price as form of 123.23, "isCustomerID",
  * @param args 
  */
	public static void main(String[] args) {
		
    	
    	// test isSku
    	String sku_str = "AB-123456-0N";
    	String not_sku_str1 = "AB-123B-0N";
    	String not_sku_str4 = "A1-123B-0N";
    	String not_sku_str2 = "AB-1234B-0N";
    	String not_sku_str3 = "11-123424-bB ";
    	String not_sku_str5 = "ACB-123424-bB ";
    	String not_sku_str7 = "PC-213000-1AB";
    	String not_sku_str6 = "ACB-123424-bB1 ";
    	
    	System.out.println("testing isSKU function \n");
    	assert isSKU(sku_str) == true : "valid sku";
    	assert isSKU(not_sku_str1) == false : "invalid sku1";
    	assert isSKU(not_sku_str2) == false : "invalid sku2";
    	assert isSKU(not_sku_str3) == false : "invalid sku3";
    	assert isSKU(not_sku_str4) == false : "invalid sku4";
    	assert isSKU(not_sku_str5) == false : "invalid sku5";
    	assert isSKU(not_sku_str6) == false : "invalid sku6";
    	assert isSKU(not_sku_str7) == false : "you are wrong !";
    	System.out.println("isSKU passed \n");
    	// if in vm's  run configuration it does not allow assertion, uncomment and run below prints    	
//    	System.out.print(isSKU(sku_str));
//    	System.out.print("\n");	
//    	System.out.print(isSKU(not_sku_str1));
//    	System.out.print("\n");	
//    	System.out.print(isSKU(not_sku_str2));
//    	System.out.print("\n");	
//    	System.out.print(isSKU(not_sku_str3));
//    	System.out.print("\n");	
//    	System.out.print(isSKU(not_sku_str4));
//    	System.out.print("\n");	
    	
    	//test isRound
    	
    	System.out.println("testing isRounded function \n");
    	
    	String price_str = "231.123";
    	String price_str1 = "12.2";
    	String isprice_str = "12.32";
    	String isprice_str1 = "1.32";
    	assert isRounded(price_str)== false : "invalid price";
    	assert isRounded(price_str1) == false : "invalid price";
    	assert isRounded(isprice_str) == true : "valid price";
    	assert isRounded(isprice_str1) == true : "valid price";
    	System.out.println("isRounded passed \n");

    	// test isCustomID
    	System.out.println("testing isCustomID function \n");

    	String[] invalid_idstrs = {"john@aol...com", "jph123@Klingon.","jph123@123" };
    	String[] valid_idstrs = {"john@aol.com", "jph123@klingon.star","jph123@live.io" };
    	for (int i = 0; i < valid_idstrs.length; i++) {
    		String str = valid_idstrs[i];
    		assert isCustomerIDEmailAddress(str) == true : "valid id";
//    		System.out.println(i + " i passed \n");
    	}
    	for (String str: invalid_idstrs ) {
    		assert isCustomerIDEmailAddress(str) == false : "invalid id";
    	}

    	System.out.println("isCustomerID passed \n");
    	// test isZip
    	System.out.println("testing isZip function \n");
    	String iszip_str = "95123";
    	String iszip_str1 = "12371";
    	String iszip_str2 = "95123-3678";
    	String notzip_str = "12";
    	String notzip_str1 = "123-321";
    	String notzip_str2 = "123481";
    	String notzip_str3 = "12348-213";
    	String notzip_str4 = "12348-21378";
    	String notzip_str5 = "12C48";
    	assert isZipCode(iszip_str) == true : "valid zip";
    	assert isZipCode(iszip_str1) == true : "valid zip";
    	assert isZipCode(iszip_str2) == true : "valid zip";
    	assert isZipCode(notzip_str) == false : "invalid zip";
    	assert isZipCode(notzip_str1) == false : "invalid zip";
    	assert isZipCode(notzip_str2) == false : "invalid zip";
    	assert isZipCode(notzip_str3) == false : "invalid zip";
    	assert isZipCode(notzip_str4) == false : "invalid zip";
    	assert isZipCode(notzip_str5) == false : "invalid zip";
    	System.out.println("isZip passed \n");
    	
    	// test isPropercount
    	String cnt_str = "12";
    	String notcnt_str = "-12";
    	String notcnt_str1 = "1a2";
    	
    	assert isProperCount(cnt_str) == true;
    	assert isProperCount(notcnt_str) == false;
    	assert isProperCount(notcnt_str1) == false;
    	
	}

}
