package de.oglimmer.bcg.servlet;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.sf.json.JSONArray;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fourspaces.couchdb.Document;

public enum Authentication {
	INSTANCE;

	private static final Logger log = LoggerFactory
			.getLogger(Authentication.class);

	public boolean checkPassword(String password, Document doc) {
		boolean passGood = false;
		if (doc.has("password2")) {
			String hashed = doc.getString("password2");
			passGood = BCrypt.checkpw(password, hashed);
		} else {
			JSONArray passwordJSON = doc.getJSONArray("password");
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
				byte[] data = messageDigest.digest(password.getBytes());
				if (passwordJSON.size() == data.length) {
					passGood = true;
					for (int i = 0; i < passwordJSON.size(); i++) {
						if (data[i] != (byte) passwordJSON.getInt(i)) {
							passGood = false;
						}
					}
				}
			} catch (NoSuchAlgorithmException e) {
				log.error("Failed to checkPassword", e);
			}
		}
		return passGood;
	}

	public boolean checkForAuthorizedUser(Document doc) throws IOException {
		return "57363895957".equals(doc.optString("picPath"));
	}

}
