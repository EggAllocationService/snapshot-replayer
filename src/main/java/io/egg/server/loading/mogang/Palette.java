package io.egg.server.loading.mogang;

import java.util.ArrayList;

public class Palette {
    public ArrayList<Integer> ids;
    public Palette() {
        ids = new ArrayList<>();
        ids.add(0, 0);
    }
    public int getOrSetMapping(int id) {
        if (ids.contains(id)) {
            return ids.indexOf(id);
        }
        ids.add(id);
        return ids.indexOf(id);
    }
    public void importIds(int[] id) {
        for (int a = 0; a < id.length; a++) {
            ids.add(a, id[a]);
        }
    }
    public byte[] toBytes() {
        int len = 0;
        for (int id: ids) {
            len += VarInt.varIntSize(id);
        }
        len += VarInt.varIntSize(ids.size());
        byte[] buffer = new byte[len];
        int offset = 0;
        offset = VarInt.putVarInt(ids.size(), buffer, offset);
        for (int id : ids) {
            offset = VarInt.putVarInt(id, buffer, offset);
        }
        return buffer;
    }

}