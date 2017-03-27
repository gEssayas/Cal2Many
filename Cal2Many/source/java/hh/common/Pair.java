package hh.common;

public class Pair<Left,Right> {
    private Left l;
    private Right r;
    public Pair(Left al, Right ar){
        this.l = al;
        this.r = ar;
    }
    public Left getLeft(){ return this.l; }
    public Right getRight(){ return this.r; }
    public void setLeft(Left l){ this.l = l; }
    public void setRight(Right r){ this.r = r; }
    public String toString() {return "<"+ l.toString()+", "+r.toString()+">";}
}