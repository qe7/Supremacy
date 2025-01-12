package com.supremacy.nanovg;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public final class FontManager {

    private static final FontManager instance = new FontManager();


    private final HashMap<String, FontRenderer> fontMap = new HashMap<>();

    public FontRenderer getFont(final String name) {
        if (fontMap.containsKey(name)) {
            return fontMap.get(name);
        }

        final Path pathURL = FabricLoader.getInstance().getModContainer("supremacy")
                .flatMap(c -> c.findPath("assets/supremacy/font/" + name + ".ttf"))
                .orElse(null);

        try {
            fontMap.put(name, new FontRenderer(name, Files.newInputStream(pathURL)));

            return fontMap.get(name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static FontManager getInstance() {
        return instance;
    }
}