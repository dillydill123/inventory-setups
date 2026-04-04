package inventorysetups.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import inventorysetups.InventorySetupsStackCompareID;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class InventorySetupItemSerializableTypeAdapter extends TypeAdapter<InventorySetupItemSerializable>
{
	@Override
	public void write(JsonWriter out, InventorySetupItemSerializable iss) throws IOException
	{
		if (iss == null)
		{
			out.setSerializeNulls(true);
			out.nullValue();
			out.setSerializeNulls(false);
		}
		else
		{
			out.beginObject();
			out.name("id");
			out.value(iss.getId());
			if (iss.getQ() != null)
			{
				out.name("q");
				out.value(iss.getQ());
			}
			if (iss.getF() != null)
			{
				out.name("f");
				out.value(iss.getF());
			}
			if (iss.getSc() != null)
			{
				out.name("sc");
				out.value(iss.getSc().toString());
			}
			out.endObject();
		}

	}

	@Override
	public InventorySetupItemSerializable read(JsonReader in) throws IOException
	{
		if (in.peek() == JsonToken.NULL)
		{
			in.nextNull();
			return null;
		}

		int id = -1;
		Integer q = null;
		Boolean f = null;
		InventorySetupsStackCompareID sc = null;

		in.beginObject();
		while (in.hasNext())
		{
			JsonToken token = in.peek();
			if (token.equals(JsonToken.NAME))
			{
				//get the current token
				String fieldName = in.nextName();
				switch (fieldName)
				{
					case "id":
						id = in.nextInt();
						break;
					case "q":
						q = in.nextInt();
						break;
					case "f":
						f = in.nextBoolean();
						break;
					case "sc":
						sc = InventorySetupsStackCompareID.valueOf(in.nextString());
						break;
					default:
						// Handle any issues from legacy migrations without getting stuck in infinite loops
						log.warn("Skipping unknown field '{}' in InventorySetupItemSerializable", fieldName);
						in.skipValue();
						break;
				}
			}
			else
			{
				// Defensive: ensure we always make progress even if we somehow end up on a value token.
				log.warn("Skipping unknown token '{}' in InventorySetupItemSerializable", token);
				in.skipValue();
			}
		}

		in.endObject();
		return new InventorySetupItemSerializable(id, q, f, sc);
	}
}

