package com.pengsheng.flutterad.utils;

/**
 * Create by WUzejian on 2022/1/17.
 */
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class UrlBuilder {
    private final Map<String, List<String>> mParamMap = new LinkedHashMap();
    private String mUrl;

    public UrlBuilder(String url) {
        this.mUrl = url;
    }

    public UrlBuilder() {
        this.mUrl = null;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public void addParam(String name, int value) {
        List<String> valueList = (List)this.mParamMap.get(name);
        if (valueList == null) {
            valueList = new LinkedList();
        }

        ((List)valueList).add(String.valueOf(value));
        this.mParamMap.put(name, valueList);
    }

    public void addParam(String name, long value) {
        List<String> valueList = (List)this.mParamMap.get(name);
        if (valueList == null) {
            valueList = new LinkedList();
        }

        ((List)valueList).add(String.valueOf(value));
        this.mParamMap.put(name, valueList);
    }

    public void addParam(String name, double value) {
        List<String> valueList = (List)this.mParamMap.get(name);
        if (valueList == null) {
            valueList = new LinkedList();
        }

        ((List)valueList).add(String.valueOf(value));
        this.mParamMap.put(name, valueList);
    }

    public void addParam(String name, String value) {
        List<String> valueList = (List)this.mParamMap.get(name);
        if (valueList == null) {
            valueList = new LinkedList();
        }

        ((List)valueList).add(String.valueOf(value));
        this.mParamMap.put(name, valueList);
    }

    public Map<String, String> getParams() {
        Map<String, String> resultMap = new LinkedHashMap();
        if (this.mParamMap != null && this.mParamMap.size() > 0) {
            Iterator var2 = this.mParamMap.entrySet().iterator();

            while(var2.hasNext()) {
                Entry<String, List<String>> entry = (Entry)var2.next();
                String key = (String)entry.getKey();
                if (!TextUtils.isEmpty(key)) {
                    List<String> valueList = (List)entry.getValue();
                    String value = "";
                    if (valueList != null && valueList.size() > 0) {
                        value = (String)valueList.get(0);
                    }

                    resultMap.put(key, value);
                }
            }
        }

        return resultMap;
    }

    public Map<String, List<String>> getParamsWithValueList() {
        return this.mParamMap;
    }

    public String build() {
        if (this.mParamMap.isEmpty()) {
            return this.mUrl;
        } else {
            String s = this.format(this.mParamMap, "UTF-8");
            if (this.mUrl != null && this.mUrl.length() != 0) {
                int index = this.mUrl.indexOf(63);
                return index >= 0 ? this.mUrl + "&" + s : this.mUrl + "?" + s;
            } else {
                return s;
            }
        }
    }

    public String format(Map<String, List<String>> paramMap, String encoding) {
        StringBuilder result = new StringBuilder();
        Iterator var3 = paramMap.entrySet().iterator();

        while(true) {
            String encodedName;
            List valueList;
            do {
                do {
                    if (!var3.hasNext()) {
                        return result.toString();
                    }

                    Entry<String, List<String>> entry = (Entry)var3.next();
                    encodedName = this.encode((String)entry.getKey(), encoding);
                    valueList = (List)entry.getValue();
                } while(valueList == null);
            } while(valueList.size() <= 0);

            Iterator var7 = valueList.iterator();

            while(var7.hasNext()) {
                String value = (String)var7.next();
                String encodedValue = value != null ? this.encode(value, encoding) : "";
                if (result.length() > 0) {
                    result.append("&");
                }

                result.append(encodedName);
                if (!encodedValue.isEmpty()) {
                    result.append("=");
                    result.append(encodedValue);
                }
            }
        }
    }

    private String encode(String content, String encoding) {
        try {
            return URLEncoder.encode(content, encoding != null ? encoding : "ISO-8859-1");
        } catch (UnsupportedEncodingException var4) {
            throw new IllegalArgumentException(var4);
        }
    }

    public String toString() {
        return this.build();
    }
}
