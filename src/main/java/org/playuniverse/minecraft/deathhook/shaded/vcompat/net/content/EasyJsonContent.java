package org.playuniverse.minecraft.deathhook.shaded.vcompat.net.content;

import java.io.IOException;

import org.playuniverse.minecraft.deathhook.shaded.vcompat.net.IEasyTextContent;

import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.io.JsonParser;
import com.syntaxphoenix.syntaxapi.json.io.JsonSyntaxException;
import com.syntaxphoenix.syntaxapi.json.value.JsonNull;

public class EasyJsonContent implements IEasyTextContent {

    public static final EasyJsonContent JSON = new EasyJsonContent();

    private final JsonParser parser = new JsonParser();

    private EasyJsonContent() {}

    @Override
    public String name() {
        return "application/json";
    }

    @Override
    public final boolean modifyUrl() {
        return false;
    }

    @Override
    public String toString(JsonValue<?> value) {
        return value.toString();
    }

    @Override
    public JsonValue<?> fromString(String data) {
        try {
            return parser.fromString(data);
        } catch (IOException | JsonSyntaxException exp) {
            return JsonNull.get();
        }
    }

}
