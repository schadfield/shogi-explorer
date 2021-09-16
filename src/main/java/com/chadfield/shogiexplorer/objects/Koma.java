package com.chadfield.shogiexplorer.objects;

public class Koma {
    
    public enum Type {
        SFU, SGI, SGY, SHI, SKA, SKE, SKI, SKY, SNG, SNK, SNY, SOU, SRY, STO, SUM,
        GFU, GGI, GGY, GHI, GKA, GKE, GKI, GKY, GNG, GNK, GNY, GOU, GRY, GTO, GUM
    }

    private final Type type;
    
    public Koma(Type komaType) {
        type = komaType;
    } 
    
    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }
}
