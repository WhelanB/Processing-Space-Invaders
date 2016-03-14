public class Pickup{
    boolean hasBeenPickedUp = false;
    int x;
    int y;
    PImage sprite;
    PowerUp type;
    public Pickup(int xpos, int ypos, PImage img, PowerUp p){
            x = xpos;
            y = ypos;
            sprite = img;
            type = p;
    }
    
    
    void draw(){
        if(!hasBeenPickedUp){
             image(sprite, x, y);
        }
    }
    
    void move(){
         y++;   
    }
    
    public void collide(Player p){
         if(y > p.y && y < p.y+sprite.height && x+sprite.width/2 < p.x + p.sprite.width && x+sprite.width/2 > p.x && !p.isDead && gameState == GameState.Playing){
              hasBeenPickedUp = true;   
              powerup.play();
              powerup.rewind();
              p.powerUp = type;
              p.shotCount = 0;
         }
     }
    
    
}