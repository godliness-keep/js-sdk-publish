package com.longrise.android.jssdk.stream;

import android.support.annotation.NonNull;

/**
 * Created by godliness on 2020-04-23.
 *
 * @author godliness
 */
public abstract class BaseStream {

    private Chunk[] mChunk;

    protected abstract void request(@NonNull Chunk[] chunks);

    public final void send() {
        request(mChunk);
    }

    protected BaseStream(String value) {
        this(4, value);
    }

    protected BaseStream(int geometry, String value) {
        final int chunkSize = geometry * 1024;
        final int valueSize = value.length();
        if (valueSize < chunkSize) {
            throw new IllegalStateException("valueSize < chunkSize)");
        }
        segmentationForValue(value.hashCode(), chunkSize, value, valueSize);
    }

    private void segmentationForValue(int sid, int chunkSize, String value, int valueSize) {
        final int count = valueSize / chunkSize;
        final boolean hasRemainder = valueSize % chunkSize > 0;
        mChunk = new Chunk[hasRemainder ? count + 1 : count];

        for (int i = 0; i < count; i++) {
            final int start = i * chunkSize;
            final int end = (i + 1) * chunkSize;
            mChunk[i] = Chunk.create(sid, i, value.substring(start, end));
        }
        if (hasRemainder) {
            mChunk[count] = Chunk.create(sid, count, value.substring(count * chunkSize));
        }
    }

    public static final class Chunk {

        private static final String SCHEMA = "chunk://";

        private final int sid;
        private final int cid;
        private final String data;

        public String getChunkData() {
            return new StringBuilder(SCHEMA)
                    .append(sid)
                    .append(":")
                    .append(cid)
                    .append("/")
                    .append(data)
                    .toString();
        }

        static Chunk create(int sid, int cid, String chunk) {
            return new Chunk(sid, cid, chunk);
        }

        private Chunk(int sid, int cid, String chunk) {
            this.sid = sid;
            this.cid = cid;
            this.data = chunk;
        }
    }
}
