package io.egg.server.database;

import org.bson.codecs.pojo.annotations.BsonId;

public class DatabaseReplay {
    @BsonId
    public String id;

    public byte[] data;
}
