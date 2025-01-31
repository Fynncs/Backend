package br.com.fynncs.core;

import br.com.fynncs.core.enums.State;
import br.com.fynncs.core.interfaces.IModelState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class ModelState<T> implements IModelState {

    private T data;
    private State state = State.ORIGINAL;
    private Boolean registerModifiedAttributes = false;
    private List<String> modifiedAttributes = new ArrayList<>();


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (state != null && !State.MODIFIED.equals(state)) {
            restore();
        }
        this.state = state;
    }

    public Boolean getRegisterModifiedAttributes() {
        return Optional.ofNullable(registerModifiedAttributes).orElse(Boolean.FALSE);
    }

    @Override
    public void markAsNew() {
        setState(State.NEW);
    }

    @Override
    public void markAsModified() {
        if (state != null) {
            if (!state.equals(State.NEW) && !state.equals(State.DELETED)) {
                setState(State.MODIFIED);
            }
            return;
        }
        setState(State.MODIFIED);
    }

    @Override
    public void markAsDeleted() throws Exception {
        if (state != null && state.equals(State.NEW)) {
            throw new Exception("New items cannot be marked as deleted!");
        }
        setState(State.DELETED);
    }

    @Override
    public void restore() {
        if (state == null || state.equals(State.DELETED)) {
            setState(State.ORIGINAL);
        }
        clearModifiedAttributes();
    }

    @Override
    public void registerModifiedAttributes(Boolean attribute) {
        registerModifiedAttributes = attribute;
    }

    @Override
    public void clearModifiedAttributes() {
        if (modifiedAttributes != null) {
            modifiedAttributes.clear();
        }
    }

    @Override
    public void addModifiedAttributes(String attribute) {
        if (modifiedAttributes == null) {
            modifiedAttributes = new ArrayList<>();
        }
        if (!modifiedAttributes.contains(attribute)) {
            modifiedAttributes.add(attribute);
        }
    }

    @Override
    public List<String> getModifiedAttributes() {
        return modifiedAttributes;
    }

    public void setModifiedAttributes(List<String> modifiedAttributes) {
        this.modifiedAttributes = modifiedAttributes;
    }

    @Override
    public Boolean checkerModifiedAttributes(String attributes) {
        return modifiedAttributes != null && modifiedAttributes.contains(attributes);
    }

    @Override
    public Boolean checkState(State state) {
        return this.state != null && this.state.equals(state);
    }

    @Override
    public Boolean isNew() {
        return checkState(State.NEW);
    }

    @Override
    public Boolean isModified() {
        return checkState(State.MODIFIED);
    }

    @Override
    public Boolean isOriginal() {
        return checkState(State.ORIGINAL);
    }

    @Override
    public Boolean isDeleted() {
        return checkState(State.DELETED);
    }
}
