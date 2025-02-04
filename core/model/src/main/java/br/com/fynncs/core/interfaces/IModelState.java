package br.com.fynncs.core.interfaces;

import br.com.fynncs.core.enums.State;

import java.io.Serializable;
import java.util.List;

public interface IModelState extends Serializable {

    public void markAsNew();

    public void markAsModified();

    public void markAsDeleted() throws Exception;

    public void restore();

    public void registerModifiedAttributes(Boolean attribute);

    public void clearModifiedAttributes();

    public void addModifiedAttributes(String attribute);

    public List<String> getModifiedAttributes();

    public Boolean checkerModifiedAttributes(String attributes);

    public Boolean checkState(State state);

    public Boolean isNew();

    public Boolean isModified();

    public Boolean isOriginal();

    public Boolean isDeleted();
}
