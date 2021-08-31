package com.chadfield.shogiexplorer.objects;

public class Koma {

    public final Type type;
    
    public Koma(Type komaType) {
        type = komaType;
    }
    
    public enum Type {
        SFU, SGI, SGY, SHI, SKA, SKE, SKI, SKY, SNG, SNK, SNY, SOU, SRY, STO, SUM,
        GFU, GGI, GGY, GHI, GKA, GKE, GKI, GKY, GNG, GNK, GNY, GOU, GRY, GTO, GUM
    }
    
    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }
}
