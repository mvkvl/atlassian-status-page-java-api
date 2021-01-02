package ws.slink.statuspage.type;

public enum HttpStatus {

     OK(200)
    ,CREATED(201)
    ,NO_CONTENT(204)
    ;

    private int value;
    HttpStatus(int value) {
        this.value = value;
    }
    public int value() {
        return this.value;
    }
    public static HttpStatus of(int input){
        for(HttpStatus v : values()){
            if( v.value() == input ){
                return v;
            }
        }
        return null;
    }


}
