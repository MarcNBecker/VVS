package de.dhbw.vvs.utility;

import java.sql.Time;
import java.util.ArrayList;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import de.dhbw.vvs.database.ConnectionPool;
import de.dhbw.vvs.database.DatabaseConnection;

public class Verifier {

	public static void main(String[] args) throws Exception {
		updatePause();
		//testEMails();
		//testPhone();
		//transport();
	}
	
	public static void transport() throws Exception {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, telefongeschaeftlich as phone FROM dozent", null);
		for(TypeHashMap<String, Object> result : resultList) {
			String phone = result.getString("phone");
			int id = result.getInt("id");
			if(!phone.equals("") && Utility.checkPhone(phone)) { //needs check phone to have "DE"
				String formattedPhone = formatPhone(phone);
				ArrayList<Object> fieldValues = new ArrayList<Object>();
				fieldValues.add(formattedPhone);
				fieldValues.add(id);
				db.doQuery("UPDATE dozent SET telefongeschaeftlich = ? WHERE id = ?", fieldValues);
			}
		}
	}
	
	public static String formatPhone(String phoneNumber) {
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		try {
			PhoneNumber number = phoneUtil.parse(phoneNumber, "DE");
			return phoneUtil.format(number, PhoneNumberFormat.INTERNATIONAL);
		} catch (NumberParseException e) {
			return "";
		}
	}
	
	public static void testEMails() throws Exception {
		int incorrect = 0;
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT kursmail as mail FROM kurs", null);
		for(TypeHashMap<String, Object> result : resultList) {
			String mail = result.getString("mail");
			if(!mail.equals("") && !Utility.checkEmail(mail)) {
				System.out.println(mail);
				incorrect++;
			}
		}
		System.out.println(incorrect + "/" + resultList.size());
	}
	
	public static void testPhone() throws Exception {
		int incorrect = 0;
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT fax as phone FROM dozent", null);
		for(TypeHashMap<String, Object> result : resultList) {
			String phone = result.getString("phone");
			if(!phone.equals("") && !Utility.checkPhone(phone)) { //needs check phone to have "DE"
				System.out.println(phone);
				incorrect++;
			} else if (!phone.equals("")) {
				//System.out.println(phone);
			}
		}
		System.out.println(incorrect + "/" + resultList.size());
	}
	
	public static void updatePause() throws Exception {
		DatabaseConnection db = ConnectionPool.getConnectionPool().getConnection();
		ArrayList<TypeHashMap<String, Object>> resultList = db.doSelectingQuery("SELECT id, startUhrzeit, endUhrzeit FROM termin", null);
		for(TypeHashMap<String, Object> result : resultList) {
			int id = result.getInt("id");
			Time start = (Time) result.get("startUhrzeit");
			Time ende = (Time) result.get("endUhrzeit");
			long minutes = (ende.getTime() - start.getTime()) / 60000;
    		if (minutes <= 90) {
    			continue;
    		} else {
    			int pause = ((int) ((minutes / 50.0))) * 5;
				ArrayList<Object> fieldValues = new ArrayList<Object>();
				fieldValues.add(pause);
				fieldValues.add(id);
				db.doQuery("UPDATE termin SET pause = ? WHERE id = ?", fieldValues);
    		}
		}
	}

}
