import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class EnumSerializer implements JsonDeserializer<Type> {
  @Override
  public Type deserialize(
      JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return Type.mapStringToType(json.getAsString());
  }
}
