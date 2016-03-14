public class Bomb{
     int x;
     int y;
     PImage sprite;
     Alien owner;
     boolean hasHit;
     int shieldPenetration = 0;
     public Bomb(int xpos, int ypos, PImage img, Alien i){
             x = xpos;
             y = ypos;
             sprite = img;
             owner = i;
     }
     
     void draw(){
          image(sprite, x, y);
     }
     
     public void collide(Player a){
         if(x > a.x && x + sprite.width < a.x + a.sprite.width && y > a.y && y + sprite.height < a.y + a.sprite.height){
                 hasHit = true; 
                 if(gameState != GameState.Lost)
                     a.explode();
                     explode.play();
                     explode.rewind();
             }
             
         
     }
     
     void move(){
          y++;
          if(hasHit){
               owner.removebomb();   
          }
          if(this.y+sprite.height > 640){
               owner.removebomb(); 
               return;
          }
          if(y+sprite.height < 480-sprite.height-1 && x < 640-sprite.width && x > 0){
                for(int xpos = x; xpos < x + sprite.width; xpos++){
                     for(int ypos = y; ypos < y + (sprite.height); ypos++){
                         if(Shield.pixels[xpos][ypos] == 1){
                             //hasHit = true;
                             shieldPenetration++;
                             Shield.pixels[xpos][ypos] = 0;   
                         }
                 }
             }
           if(shieldPenetration/sprite.width == 5){
               hasHit = true;
           }    
          }
     }
    
}