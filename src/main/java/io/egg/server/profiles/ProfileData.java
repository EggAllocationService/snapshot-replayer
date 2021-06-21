package io.egg.server.profiles;

public class ProfileData {
    public String mapName;
    public boolean saveMap;
    public Object specificData;
    public ProfileData(String map, Boolean save) {
        mapName = map;
        saveMap = save;
    }
}
