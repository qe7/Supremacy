package com.supremacy.nanovg;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public final class ImageManager {

    private final HashMap<String, NVGImage> imageMap = new HashMap<>();

    private static final ImageManager instance = new ImageManager();

    public static ImageManager getInstance() {
        return instance;
    }

    public NVGImage getImage(final Path path, final int flags) {
        final String pathString = path.toString();

        if (imageMap.containsKey(pathString)) {
            return imageMap.get(pathString);
        }

        try {
            imageMap.put(pathString, new NVGImage(Files.newInputStream(path), flags));
            return imageMap.get(pathString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NVGImage getImage(final String path) {
        if (imageMap.containsKey(path))
            return imageMap.get(path);

        final Path pathURL = FabricLoader.getInstance().getModContainer("supremacy")
                .flatMap(c -> c.findPath("assets/supremacy/images/" + path))
                .orElse(null);

        try {
            imageMap.putIfAbsent(path, new NVGImage(Files.newInputStream(pathURL)));

            return imageMap.get(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}