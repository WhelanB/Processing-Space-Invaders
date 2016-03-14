import ddf.minim.*;
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


    
void setup(){
    size(640, 480, FX2D);
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

void setupaliens(){
     for(int i = 0; i < 10; i++){
        for(int j = 0; j < round; j++){
            ParticleHandler ph = new ParticleHandler();
            particleHandlers.add(ph);
            aliens.add(new Alien(i* (alien.width*2), 64 * j, alien, (int)random(0,5), ph, loadImage("bomb.png")));
        }
        
    }    
}

void draw(){
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


void mousePressed(){
    if(gameState == GameState.Playing){
        ship.shoot();
    }
 }