package io.egg.server.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.egg.server.loading.World;
import io.egg.server.loading.WorldChunk;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Database {
    static Database instance;

    public static Database getInstance() {
        return instance;
    }
    public static void init(String databaseName) {
        instance = new Database(databaseName);
    }

    MongoClient client;
    MongoDatabase db;
    public MongoCollection<World> worlds;
    public MongoCollection<WorldChunk> worldChunks;
    public MongoCollection<DatabaseReplay> replays;
    public Database(String databaseName) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        client = MongoClients.create("mongodb://192.168.3.10:25563/?retryWrites=true&w=majority");

        db = client.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        worlds = db.getCollection("worlds", World.class);
        worldChunks = db.getCollection("chunks", WorldChunk.class);
        replays = db.getCollection("saved", DatabaseReplay.class);
    }

}
