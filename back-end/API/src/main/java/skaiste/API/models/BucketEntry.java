package skaiste.API.models;

import java.util.UUID;

public class BucketEntry {
    private UUID queryNode;
    private UUID dataNode;
    private int weight;
    private int similarity;

    public BucketEntry(UUID queryNode, UUID dataNode, int weight, int similarity) {
        this.queryNode = queryNode;
        this.dataNode = dataNode;
        this.weight = weight;
        this.similarity = similarity;
    }

    public UUID getQueryNode() {
        return queryNode;
    }

    public UUID getDataNode() {
        return dataNode;
    }

    public int getWeight() {
        return weight;
    }

    public int getSimilarity() {
        return similarity;
    }

}
