package br.com.fynncs.core.model;

import br.com.fynncs.core.enums.ResourceType;

import java.util.Map;

public class Resource {
    private String id;
    private ResourceType type;
    private String description;
    private Map<String, String> info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }
}
