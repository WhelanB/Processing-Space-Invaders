import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import java.util.Iterator; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch_160201a extends PApplet {


PImage alien;
PImage aliensine;
Alien a;
PFont text;
ArrayList<Alien> aliens = new ArrayList<Alien>();
ArrayList<Pickup> pickups = new ArrayList<Pickup>();
ArrayList<ParticleHandler> particleHandlers = new ArrayList<ParticleHandler>();
Player ship;
PImage map;
AudioPlayer shot;
AudioPlayer explode;
AudioPlayer powerup;
AudioPlayer homing;
int round = 1;
Shield s;
Minim minim;
GameState gameState = GameState.Playing;
static enum GameState{
     Paused,
     Playing,
     Won,
     Lost
     
}
static enum AlienState{
         Strafe,
         MoveDown,
         Exploded
}


    
public void setup(){
    
    minim = new Minim(this);
    text = loadFont("font.vlw");
    shot = minim.loadFile("shoot.wav");
    explode = minim.loadFile("explode.wav");
    powerup = minim.loadFile("powerup.wav");
    homing = minim.loadFile("homing.wav");
    map = loadImage("shields.png");
    alien = loadImage("alien.png");
    aliensine = loadImage("aliensine.png");
    ship = new Player(32, 480-32, loadImage("ship.png"));
    
    noStroke();
    setupaliens(); 
    for(int x = 0; x < 640; x++){
        for(int y = 0; y < 480; y++){
            if(alpha(map.get(x,y)) > 0){
                 Shield.pixels[x][y] = 1;   
            }
        }
    }
    
}

public void setupaliens(){
     for(int i = 0; i < 10; i++){
        for(int j = 0; j < round; j++){
            ParticleHandler ph = new ParticleHandler();
            particleHandlers.add(ph);
            aliens.add(new Alien(i* (alien.width*2), 64 * j, alien, (int)random(0,5), ph, loadImage("bomb.png")));
        }
        
    }    
}

public void draw(){
    fill(0,0,0,75);
    rect(0,0,640, 480);
    //background(0);
    fill(255);
    text("Round " + round + "/5", 0, 10);
    if(gameState == GameState.Won)
        text("You Won! \nCongratulations", SCREENX/2-20, 480/2);
    if(gameState == GameState.Lost)
        text("Game Over. \nYou Survived to Round " + round, SCREENX/2-20, 480/2);
    for(Iterator<Alien> a = aliens.iterator(); a.hasNext();){
             Alien alien = a.next();
             alien.draw();
             if(alien.myBomb != null)
                 alien.myBomb.collide(ship);
             alien.move();
             if(alien.state == AlienState.Exploded){
                a.remove();
             }
    }
    if(aliens.size() == 0){
         //gameState = GameState.Won;
         if(round == 5){
              gameState = GameState.Won;   
         }
         else{
             round++;
             setupaliens();
         }
    }
    for(Pickup p : pickups){
         p.move();
         p.draw();
         p.collide(ship);
    }
    for(ParticleHandler p : particleHandlers){
            p.draw();
    }
    ship.move();
    ship.draw();
    for(int x = 0; x < 640; x++){
         for(int y = 0; y < 480; y++){
             if(Shield.pixels[x][y] != 0){
                  fill(255,255,255, 255);
                  rect(x,y,1,1);
             }
         }
    } 
    for(Bullet b : ship.bullets){
         for(Alien a : aliens){
              b.collide(a);   
         }
    }
}


public void mousePressed(){
    if(gameState == GameState.Playing){
        ship.shoot();
    }
 }
class Alien{
    public float x = 0;
    public float y = 0;
    float dx = 1;
    boolean dropsPowerUp;
    int startY = 0;
    int explodeTime = (int)random(10, 25) * 1000;
    int startTime;
    int explosionTime = 0;
    int direction = 1;
    PImage sprite;
    PImage particle = loadImage("particle.png");
    PImage bombSprite;
    Bomb myBomb;
    ParticleHandler particleHandler;
    AlienState state = AlienState.Strafe;
    
    public Alien(int x, int y, PImage img, int power, ParticleHandler p, PImage bomb){
        dropsPowerUp = power == 0;
        bombSprite = bomb;
        startTime = millis();
        this.x = x;
        this.y = y;
        sprite = img;
        particleHandler = p;
    }
    
    public void dropBomb(){
         myBomb = new Bomb((int)x+(sprite.width/2),(int) y + sprite.height, bombSprite, this);   
    }
    
    public void draw(){
        if(state != AlienState.Exploded){
            if(myBomb != null)
                myBomb.draw();
            image(sprite, x, y); 
        }
        
    }
    
    public void removebomb(){
         myBomb = null;   
    }
    public void move(){
        
        dx+= 0.001f;
        if(this.y > 480){
             gameState = GameState.Lost;   
        }
        if(myBomb != null)
            myBomb.move();
        explosionTime = millis();
        if(explosionTime - startTime >= explodeTime && state != AlienState.Exploded && myBomb == null){
           dropBomb();
           explosionTime = millis()-explosionTime;
        }
        if(state == AlienState.Strafe){
          x+= dx * direction; 
        }
        if(state == AlienState.MoveDown){
             y+= dx;
             if((y - sprite.height) > startY){
                 y = startY + sprite.height;
                 state = AlienState.Strafe;
                 direction *= -1;
                 x+=dx * direction;
                 
             }
                 
             
        }
         if((x+sprite.width > SCREENX || x < 0 )&& state != AlienState.MoveDown && state != AlienState.Exploded){
             state = AlienState.MoveDown; 
             startY = (int)this.y;
         }
        
    }
    
    public void Explode(){
         explode.play();
         explode.rewind();
         if(dropsPowerUp){
              int rand = (int)random(0,2);
              pickups.add(new Pickup((int)x,(int)y,loadImage("powerup" + rand + ".png"), PowerUp.values()[rand+1]));   
         }
             
         state = AlienState.Exploded;   
         for(int i = 0; i < sprite.width/2; i++){
                for(int j = 0; j < sprite.height/2; j++){
                    int c = color(red(sprite.get(i*2,j*2)), green(sprite.get(i*2,j*2)), blue(sprite.get(i*2,j*2    )));
                        particleHandler.Register(new Particle((int)x + (i*2),(int)y + (j * 2),random(-2, 2), random(-2,2), c )); 
                }
        }
             
    }
    
    
}
    
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
     
     public void draw(){
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
     
     public void move(){
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
int SCREENX = 640;
public class HomingBullet extends Bullet{
    Alien target;
    public HomingBullet(int x,int y, PImage img, Player o){
        
        super(x,y,img,o);
        homing.play();
        homing.rewind();
        if(aliens.size() > 0){
            target = aliens.get((int)random(0,aliens.size()));
            while(target != null && target.state == AlienState.Exploded)
                target = aliens.get((int)random(0,aliens.size()));
        }
        
    }
    
    public void move(){
        if(gameState == GameState.Playing){
        ypos-=5;
       
        if(target != null){
                 if(this.xpos > target.x)
                     xpos-=5;
                 else
                     xpos+=5;
            }
            else
            {
                 if(this.xpos > SCREENX/2)
                     xpos-=5;
                 else
                     xpos+=5;
            }
            
        }
    }
}
public class Particle{
    float x;
    float y;
    float dx;
    float dy;
    float angle = 0;
    int alpha = 255;
    int sprite;
     public Particle(float x, float y, float dx, float dy, int img){
         this.x = x;
         this.y = y;
         this.dx = dx;
         this.dy = dy;
         sprite = img;
     }
     
     public void draw(){
        alpha -= 5;
        x += dx;
        y += dy;
        dy+= 0.02f;
        angle += 0.1f;
        pushMatrix();
        noStroke();
        translate(x,y);
        rotate(angle);
        fill(red(sprite), green(sprite), blue(sprite), alpha);
        rect(-2,-2,2,2);  
        popMatrix();
          
     }
     
     public boolean isInvisible(){
         return alpha < 0;
     }

}

class ParticleHandler{
 
    ArrayList<Particle> particles = new ArrayList<Particle>();
    
    public ParticleHandler(){
        
    }
    
    public void Register(Particle p){
         particles.add(p);   
    }
    
    public void draw(){
         for(Iterator<Particle> p = particles.iterator(); p.hasNext();){
             Particle particle = p.next();
             particle.draw();
             if(particle.isInvisible()){
                p.remove();
             }
         }
          
    }
    
}
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
    
    
    public void draw(){
        if(!hasBeenPickedUp){
             image(sprite, x, y);
        }
    }
    
    public void move(){
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
 
 public void draw(){
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
 public void move(){
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
                    int c = color(red(sprite.get(i*2,j*2)), green(sprite.get(i*2,j*2)), blue(sprite.get(i*2,j*2    )));
                        particleHandler.Register(new Particle((int)x + (i*2),(int)y + (j * 2),random(-2, 2), random(-2,2), c )); 
                }
        }
 }
 
 
    
    
}
public static class Shield{
    static byte[][] pixels = new byte[640][480];
     
   
     
     
    
}
class SineAlien extends Alien{
    int lastX;
    float dy = 0.01f;
    public SineAlien(int x, int y, PImage img, int powerUp, ParticleHandler p, PImage b){
         super(x,y,img, powerUp,p, b);
         lastX = (int)x;
    }
   
    public void move(){
        y+= dy;
         x = (sin(y) * 10) + lastX;
    }
    
    
    
}
    public void settings() {  size(640, 480, FX2D); }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "sketch_160201a" };
        if (passedArgs != null) {
          PApplet.main(concat(appletArgs, passedArgs));
        } else {
          PApplet.main(appletArgs);
        }
    }
}
