package com.supremacy.bind;

public interface BindService {
    void bind(final Bindable bindable, final int key);
    void unbind(final Bindable bindable);
    int getKey(final Bindable bindable);
    boolean isBound(final Bindable bindable);

}
