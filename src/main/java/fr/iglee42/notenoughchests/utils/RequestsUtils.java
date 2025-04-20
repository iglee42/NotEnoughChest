package fr.iglee42.notenoughchests.utils;

import fr.iglee42.notenoughchests.NotEnoughChests;
import fr.iglee42.notenoughchests.custompack.PathConstant;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.iglee42.notenoughchests.NotEnoughChests.WOOD_TYPES;
import static fr.iglee42.notenoughchests.NotEnoughChests.chestTextureIds;

public class RequestsUtils {

    public static final String API_URL = "https://api.iglee.fr/";
    public static boolean API_ONLINE = true;
    private static final int DEFAULT_TIMEOUT = 2000;

    public static HttpURLConnection sendRequest(String subDomain,String method, int timeOut) throws IOException {
        if (timeOut == 0) timeOut = DEFAULT_TIMEOUT;
        URL url = URI.create(API_URL + subDomain).toURL();
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod(method);
        con.setConnectTimeout(timeOut);
        con.setReadTimeout(1000);
        con.setInstanceFollowRedirects(false);
        return con;
    }

    public static void downloadUsingStream(URL url, File file) throws IOException {
        if (file.exists()) file.delete();
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    public static void unzip(File zipFile, File destDir) throws IOException {
        String destDirectory = destDir.getAbsolutePath();

        try (ArchiveInputStream i = new ZipArchiveInputStream(new
                FileInputStream(zipFile), "UTF-8", false, true)) {
            ArchiveEntry entry = null;
            while ((entry = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(entry)) {
                    System.out.println("Can't read entry: " + entry);
                    continue;
                }
                String name = destDirectory + File.separator + entry.getName();
                File f = new File(name);
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) {
                        throw new IOException("failed to create directory " + f);
                    }
                } else {
                    File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("failed to create directory " + parent);
                    }
                    try (OutputStream o = Files.newOutputStream(f.toPath())) {
                        IOUtils.copy(i, o);
                    }
                }
            }
        }
    }

    public static void ping() {
        try {
            HttpURLConnection con = sendRequest("ping","GET",1000);
            API_ONLINE = con.getResponseCode() == 200;
        } catch (IOException e) {
            NotEnoughChests.LOGGER.error("API offline, some features may not work");
        }
    }

    public static void downloadTextures(){
        if (RequestsUtils.API_ONLINE) {
            Thread dlThread = new Thread(() -> {
                List<Integer> idsToDownload = new ArrayList<>();
                WOOD_TYPES.forEach(wt -> {
                    String abrev = ModAbbreviation.getModAbbreviation(wt.getNamespace());
                    Map<String, Integer> ids = new HashMap<>();
                    if (chestTextureIds != null)
                        chestTextureIds.asMap().forEach((id, el) -> ids.put(id, el.getAsInt()));
                    int id;
                    if (abrev.isEmpty()) {
                        id = ids.getOrDefault(wt.getPath(), -1);
                    } else {
                        id = ids.getOrDefault(abrev.replace("_", "") + "/" + wt.getPath(), -1);
                    }
                    idsToDownload.add(id);
                });
                NotEnoughChests.LOGGER.info("{} chests' textures to download !", idsToDownload.size());
                StringBuilder array = new StringBuilder("[");
                for (int i = 0; i < idsToDownload.size(); i++) {
                    array.append(idsToDownload.get(i));
                    if (i < idsToDownload.size() - 1) array.append(",");
                }
                array.append("]");
                try {
                    URL url = new URL(API_URL + "chestTextures?chests=" + array);
                    File zipFile = new File(PathConstant.ROOT_PATH.toString(), "chests.zip");
                    RequestsUtils.downloadUsingStream(url, zipFile);
                    RequestsUtils.unzip(zipFile, PathConstant.CHEST_TEXTURES_PATH.toFile());
                    NotEnoughChests.LOGGER.info("{} chests' textures downloaded !", idsToDownload.size());
                    Thread.currentThread().interrupt();
                } catch (Exception ex) {
                    NotEnoughChests.LOGGER.error("Failed to download chests' textures !");
                    Thread.currentThread().interrupt();
                }
            }, "downloadChestTexturesThread");
            dlThread.start();
        } else {
            NotEnoughChests.LOGGER.error("API isn't online, textures can't be downloaded");
        }
    }
}
