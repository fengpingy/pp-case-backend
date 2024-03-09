package com.pp.utils;

import org.json.XML;

public class XMLUtils {

    /**
     * xml转JSON
     * @param xmlString
     * @return
     */
    public static org.json.JSONObject xmlToJson(String xmlString){
        return XML.toJSONObject(xmlString);
    }
}
