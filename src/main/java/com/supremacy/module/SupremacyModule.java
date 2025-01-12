package com.supremacy.module;

import com.supremacy.bind.Bindable;
import com.supremacy.provider.suffix.SuffixProvider;
import com.supremacy.toggle.Toggleable;

public interface SupremacyModule extends Toggleable, Bindable {
    SuffixProvider suffixProvider = new SuffixProvider();
}
