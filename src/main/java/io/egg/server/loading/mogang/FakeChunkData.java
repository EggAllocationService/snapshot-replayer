package io.egg.server.loading.mogang;



import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;


public class FakeChunkData{
    public ChunkSection[] sections;
    public FakeChunkData() {
        super();
        sections = new ChunkSection[16];


    }
    public ChunkSection getSection(int y) {
        if (sections[y] == null) {
            sections[y] = new ChunkSection(y);
        }

        return sections[y];
    }
    public byte[] toBytes() {
        ArrayList<byte[]> s = new ArrayList<>();
        int l = 0;
        for (ChunkSection c : sections) {
            if (c == null) continue;
            byte[] i = c.toBytes();
            s.add(i);
            l += i.length;
        }
        ByteBuffer buf = ByteBuffer.wrap(new byte[l]);
        for (byte[] a : s) {
            buf.put(a);
        }
        return buf.array();

    }
    protected void setChunkSection(int index, ChunkSection c) {
        sections[index] = c;
    }
    public void fill(int material) {
        for(int i = 0; i < 16; i++) {
            getSection(i).fill(material);
        }
    }
    public int createBitfield() {
        int bitmask = 0;
        for (int i = 0; i < sections.length; i++) {
            if (sections[i] == null) {
                continue;
            }
            bitmask = bitmask | ( 1 << i);
        }
        return bitmask;
    }


    public static FakeChunkData fromPacket(byte[]d) {
        // b = long & 31 << 5;
        FakeChunkData chunk = new FakeChunkData();


        ByteBuffer buf = ByteBuffer.wrap(d);
        if (d.length == 0) {
            return null;
        }
        // Bukkit.getLogger().info("Packet length: " + d.length);
        int chunky = 0;

            int bitmask = 65535;

        int offset = 0;
        boolean n = false;
        while (true) {

            buf.position(offset);
            if (chunky > 15) {
                break;
            }
            if (buf.array().length == offset) {
                // Bukkit.getLogger().warning("reached end of packet!");
                break;

            }
            if ((bitmask & (int) Math.pow(2,  chunky)) == 0) {
                chunky++;
                continue;
            }
            ChunkSection e = new ChunkSection(chunky);
            e.setBlockCount(buf.getShort());
            int bpb = buf.get();
            // Bukkit.getLogger().info("" + bpb + " - " + buf.get(2));
            buf.position(offset + 3);
            int paletteLen = VarInt.getVarInt(buf);
            e.decoded_bpb = bpb;

            offset += VarInt.varIntSize(paletteLen);
            int[] palette = new int[paletteLen];
            offset+= 3;
            for (int a = 0; a < paletteLen; a++) {
                palette[a] = VarInt.getVarInt(buf);
                offset+= VarInt.varIntSize(palette[a]);
            }
            Palette pa = new Palette();
            pa.importIds(palette);
            e.setPalette(pa);
            int dataLength = VarInt.getVarInt(buf) * 8;
            offset += dataLength;
            offset += VarInt.varIntSize(dataLength / 8);
            ByteBuffer data = ByteBuffer.wrap(new byte[dataLength]);
            for (int a = 0; a < dataLength; a++) {
                data.put(buf.get());
            }
            data.order(ByteOrder.LITTLE_ENDIAN);
            byte[] clone = data.array().clone();
            data.clear();
            for (byte[] l : e.splitBytes(clone, 8)) {
                //ArrayUtils.reverse(l);
                data.put(l);
            }

            if (bpb == 4) {
                ByteBuffer blocks = ByteBuffer.wrap( new byte[4096]);

                for(byte[] blockChunk : e.splitBytes(data.array(), 8)) {

                    long lint = ByteBuffer.wrap(blockChunk).getLong();
                    for (int i = 0; i < 16; i++) {
                        byte b = (byte) (lint & 15);
                        lint = lint >> 4;
                        blocks.put(b);
                    }
                }
                e.setBlocks(blocks.array());
                chunk.setChunkSection(chunky, e);

            } else if (bpb == 5) {
                /*if (!n) {
                    try {
                        FileUtils.writeByteArrayToFile(new File("/mnt/c/Users/Kyle Smith/Desktop/stream_in" + chunk.getChunkX() + chunky + chunk.getChunkZ() + ".hexdump"), data.array());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }*/
                ByteBuffer blocks = ByteBuffer.wrap(new byte[4096]);

                for(byte[] blockChunk : e.splitBytes(data.array(), 8)) {
                    ByteBuffer u = ByteBuffer.wrap(blockChunk);

                    u.position(0);
                    long lint = u.getLong();
                    for (int i = 0; i < 12; i++) {
                        byte b = (byte) (lint & 31);
                        lint = lint >> 5;
                        if (blocks.position() == 4096) {
                            continue;
                        }
                        blocks.put(b);
                    }
                }
                e.setBlocks(blocks.array());
                chunk.setChunkSection(chunky, e);
                /*if (!n) {
                    try {
                        FileUtils.writeByteArrayToFile(new File("/mnt/c/Users/Kyle Smith/Desktop/blocks_out" + chunk.getChunkX() + chunky + chunk.getChunkZ() + ".hexdump"), blocks.array());
                        n = true;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }*/
                // }

            } else if (bpb == 6) {
                /*try {
                    FileUtils.writeByteArrayToFile(new File("/mnt/c/Users/Kyle Smith/Desktop/stream_in" + chunk.getChunkX() + chunky + chunk.getChunkZ() + ".hexdump"), data.array());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }*/
                ByteBuffer blocks = ByteBuffer.wrap(new byte[4096]);

                for(byte[] blockChunk : e.splitBytes(data.array(), 8)) {
                    ByteBuffer u = ByteBuffer.wrap(blockChunk);

                    u.position(0);
                    long lint = u.getLong();
                    for (int i = 0; i < 10; i++) {
                        byte b = (byte) (lint & 63);
                        lint = lint >> 6;
                        if (blocks.position() == 4096) {
                            continue;
                        }
                        blocks.put(b);
                    }
                    e.setBlocks(blocks.array());
                    chunk.setChunkSection(chunky, e);
                }
            } else if (bpb == 7) {
                /*try {
                    FileUtils.writeByteArrayToFile(new File("/mnt/c/Users/Kyle Smith/Desktop/stream_in" + chunk.getChunkX() + chunky + chunk.getChunkZ() + ".hexdump"), data.array());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }*/
                ByteBuffer blocks = ByteBuffer.wrap(new byte[4096]);

                for(byte[] blockChunk : e.splitBytes(data.array(), 8)) {
                    ByteBuffer u = ByteBuffer.wrap(blockChunk);

                    u.position(0);
                    long lint = u.getLong();
                    for (int i = 0; i < 9; i++) {
                        byte b = (byte) (lint & 127);
                        lint = lint >> 7;
                        if (blocks.position() == 4096) {
                            continue;
                        }
                        blocks.put(b);
                    }
                    e.setBlocks(blocks.array());
                    chunk.setChunkSection(chunky, e);
                }
            } else {
               // Bukkit.getLogger().severe("Packet has length longer than 5, unable to parse (" + bpb + ")");
                return null;
            }
            chunky++;

        }
        return chunk;


    }
}