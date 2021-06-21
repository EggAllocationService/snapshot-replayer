package io.egg.server.snapshots;

import io.egg.server.profiles.DefaultProfileDelegate;
import io.egg.server.profiles.ProfileData;
import net.minestom.server.instance.Instance;

public class DebugSnapshotProfile extends DefaultProfileDelegate {
    @Override
    public ProfileData getData() {
        return new ProfileData("yeet", false);
    }

    @Override
    public void setupInstance(Instance i) {

    }

    @Override
    public String getName() {
        return "Replay Snapshot Viewer";
    }
}
