package de.dhbw.vvs.utility;

import java.lang.reflect.Type;
import java.sql.Time;

import org.restlet.resource.ResourceException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import de.dhbw.vvs.application.ExceptionStatus;
import de.dhbw.vvs.application.WebServiceException;
import de.dhbw.vvs.model.Geschlecht;
import de.dhbw.vvs.model.Status;
import de.dhbw.vvs.model.WebServiceEnum;

/**
 * This class is used to JSONify any objects
 */
public class JSONify {

	public static final String SUCCESS = "{\"status\":200, \"message\":\"OK\"}";
	
	private static Gson gson;
	
	//Build a Gson that can handle all model classes
	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ResourceException.class, new JsonSerializer<ResourceException>() {
			@Override
			public JsonElement serialize(ResourceException src, Type typeOfSrc, JsonSerializationContext context) {
				JsonObject jO = new JsonObject();
				int splitPosition = src.getStatus().getDescription().indexOf(" ");
				String statusString = src.getStatus().getDescription().substring(0, splitPosition);
				String message = src.getStatus().getDescription().substring(splitPosition+1);
				int status = 0;
				try {
					status = Integer.parseInt(statusString);
				} catch (NumberFormatException e) {
					status = src.getStatus().getCode();
					message = statusString + " " + message;
				}
				jO.addProperty("status", status);
				jO.addProperty("message", message);
				return jO;
			}			
		});
		gsonBuilder.registerTypeHierarchyAdapter(WebServiceEnum.class, new JsonSerializer<WebServiceEnum>() {
			@Override
			public JsonElement serialize(WebServiceEnum src, Type typeOfSrc, JsonSerializationContext context) {
				return new JsonPrimitive(src.getValue());
			}			
		});
		gsonBuilder.registerTypeHierarchyAdapter(Time.class, new JsonSerializer<Time>() {
			@Override
			public JsonElement serialize(Time src, Type typeOfSrc, JsonSerializationContext context) {
				return new JsonPrimitive(Utility.timeString(src));
			}			
		});
        gsonBuilder.registerTypeHierarchyAdapter(WebServiceEnum.class, new JsonDeserializer<WebServiceEnum>() {
            @Override
            public WebServiceEnum deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            	if(typeOfT.equals(Geschlecht.class)) {
            		return Geschlecht.getFromOrdinal(json.getAsInt());
            	}            	
            	if(typeOfT.equals(Status.class)) {
            		return Status.getFromOrdinal(json.getAsInt());
            	}
            	return null;
            }
        });
        gsonBuilder.registerTypeHierarchyAdapter(Time.class, new JsonDeserializer<Time>() {
            @Override
            public Time deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            	try {
            		return Utility.stringTime(json.getAsString());
            	} catch (WebServiceException e) {
            		return null;
            	}
            }
        });
        gsonBuilder.setDateFormat(Utility.DATE_STRING);
        gsonBuilder.serializeNulls();
		gson = gsonBuilder.create();	
	}
	
	/**
	 * Returns a JSON representation of an object
	 * @param o the object to be serialized
	 * @return the json string
	 */
	public static synchronized String serialize(Object o) {
		return gson.toJson(o);
	}
	
	/**
	 * Deserializes the JSON representation of an object
	 * @param json the json representation
	 * @param classDefinition the class of the object to be deserialized
	 * @return the deserialized object
	 * @throws JsonParseException if the json string doesn't fit the object
	 */
	public static synchronized <T> T deserialize(String json, Class<T> classDefinition) throws WebServiceException {
		try {
			return gson.fromJson(json, classDefinition);
		} catch (JsonParseException e) {
			throw new WebServiceException(ExceptionStatus.JSON_PARSING_FAILED);
		}
	}
	
	/**
	 * Deserializes the JSON representation of an object
	 * @param json the json representation
	 * @param typeToken a typeToken to specifiy the output. This must be used when using classes with generics
	 * @return the deserialized object
	 * @throws JsonParseException if the json string doesn't fit the object
	 */
	public static synchronized <T> T deserialize(String json, TypeToken<T> typeToken) throws WebServiceException {
		try {
			return gson.fromJson(json, typeToken.getType());	
		} catch (JsonParseException e) {
			throw new WebServiceException(ExceptionStatus.JSON_PARSING_FAILED);
		}
	}
	
}
