 enum PowerUp{
        none,
        homing,
        twoShot
 }
public class Player{
 int shotCount = 0;
 int x;
 int y;
 PImage sprite;
 PImage bullet;
 boolean isDead = false;

 ArrayList<Bullet> bullets = new ArrayList<Bullet>();
 ParticleHandler particleHandler;
public PowerUp powerUp;
 public Player(int x, int y, PImage img){
     this.x = x;
     this.y = y;
     sprite = img;
     bullet = loadImage("bullet.png");
     powerUp = PowerUp.none;
     particleHandler = new ParticleHandler();
     particleHandlers.add(particleHandler);
 }
 
 void draw(){
      if(gameState != GameState.Lost)
          image(sprite, x, y);  
      for(Iterator<Bullet> b = bullets.iterator(); b.hasNext();){
             Bullet bullet = b.next();
             bullet.draw();
             bullet.move();
             if(bullet.hasHit)
                 b.remove();
         }
      
 }
 void move(){
     if(gameState != GameState.Lost){
      this.x = constrain(mouseX, 0, 640-sprite.width);  
     }
 }
 
 public void shoot(){
     shot.play();
     shot.rewind();
     if(shotCount == 5){
         shotCount = 0;
         powerUp = PowerUp.none;
     }
     shotCount++;
     if(powerUp == PowerUp.none)
         bullets.add(new Bullet(x+(sprite.width/2),y,bullet, this));
     else if(powerUp == PowerUp.homing)
         bullets.add(new HomingBullet(x+(sprite.width/2),y,bullet, this));
     else if(powerUp == PowerUp.twoShot){
         bullets.add(new Bullet(x+(sprite.width/2)+20,y,bullet, this));
         bullets.add(new Bullet(x+(sprite.width/2)-20,y,bullet, this));
     }
 }
 
 public void explode(){
     isDead = true;
      gameState = GameState.Lost;
      for(int i = 0; i < sprite.width/2; i++){
                for(int j = 0; j < sprite.height/2; j++){
                    color c = color(red(sprite.get(i*2,j*2)), green(sprite.get(i*2,j*2)), blue(sprite.get(i*2,j*2    )));
                        particleHandler.Register(new Particle((int)x + (i*2),(int)y + (j * 2),random(-2, 2), random(-2,2), c )); 
                }
        }
 }
 
 
    
    
}