public class Bullet{
    boolean hasHit = false;
    int xpos;
    int ypos;
    PImage sprite;
    Player owner;
    public Bullet(int x,int y, PImage img, Player o){
         owner = o;
         xpos = x;
         ypos = y;
         sprite = img;
     }
     
     public void move(){
             if(ypos > 0)
                 ypos-=5;
             else{
                 hasHit = true;
             }
             if(ypos > 5){
             if(xpos < 640 && this.xpos > 0){
             for(int x = xpos; x < xpos + sprite.width; x++){
                 
                 if(Shield.pixels[x][ypos] == 1){
                     hasHit = true;
                     Shield.pixels[x][ypos] = 0;   
                 }
                 if(Shield.pixels[x][ypos-1] == 1){
                     hasHit = true;
                     Shield.pixels[x][ypos-1] = 0;   
                 }
                 if(Shield.pixels[x][ypos-2] == 1){
                     hasHit = true;
                     Shield.pixels[x][ypos-2] = 0;   
                 }
                 if(Shield.pixels[x][ypos-3] == 1){
                     hasHit = true;
                     Shield.pixels[x][ypos-3] = 0;   
                 }
                 if(Shield.pixels[x][ypos-4] == 1){
                     hasHit = true;
                     Shield.pixels[x][ypos-4] = 0;   
                 }
             }
             }
         }
     }
     
     public void draw(){
          image(sprite, xpos, ypos);
     }
     
     public void collide(Alien a){
         if(xpos > a.x && xpos + sprite.width < a.x + a.sprite.width && ypos > a.y && ypos + sprite.height < a.y + a.sprite.height){
             if(a.state != AlienState.Exploded) {
                 a.Explode();   
                 hasHit = true;  
             }
             
         }
     }
}