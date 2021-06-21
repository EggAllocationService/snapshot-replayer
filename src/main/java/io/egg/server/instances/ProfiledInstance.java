package io.egg.server.instances;

import io.egg.server.profiles.DefaultProfileDelegate;
import io.egg.server.profiles.ProfileData;
import net.minestom.server.instance.Instance;


public class ProfiledInstance {
    private final String name;
    private final Instance i;
    private final ProfileData pd;
    private final DefaultProfileDelegate p;
    public ProfiledInstance(Instance ii, DefaultProfileDelegate a, ProfileData ppd, String n) {
        i = ii;
        pd = ppd;
        p = a;
        name = n;
    }

    public ProfileData  getProfileData() {
        return pd;
    }

    public DefaultProfileDelegate getDelegate() {
        return p;
    }

    public String getName() {
        return name;
    }
}
