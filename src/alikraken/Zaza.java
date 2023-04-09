package alikraken;

import java.util.ArrayList;
import java.util.Scanner;
import java.awt.geom.Point2D;

public class Zaza {  /** Simple representation for a puck. */
  static class Puck {
    // Position of the puck.
    Point2D pos;

    // Puck velocity
    Point2D vel;

    // Puck color
    int color;
  };

  /** Simple representation for a bumper. */
  static class Bumper {
    // Position of the bumper.
    Point2D pos;
    
    // Bumper velocity
    Point2D vel;
  };

  /** Simple representation for a sled. */
  static class Sled {
    // Position of the sled.
    Point2D pos;
    
    // Sled direction.
    double dir;
  };

  /** How much acceleration we are willing to spend on each component. */
  private static final double ACCEL = Const.BUMPER_ACCEL_LIMIT / Math.sqrt( 2 );

  /** Return the value of a, clamped to the [ b, c ] range */
  private static double clamp( double a, double b, double c ) {
    if ( a < b )
      return b;
    if ( a > c )
      return c;
    return a;
  }

  /** Return a new vector containing the sum of a and b. */
  static Point2D sum( Point2D a, Point2D b ) {
    return new Point2D.Double( a.getX() + b.getX(), 
                               a.getY() + b.getY() );
  }

  /** Return a new vector containing the difference between a and b. */
  static Point2D diff( Point2D a, Point2D b ) {
    return new Point2D.Double( a.getX() - b.getX(), 
                               a.getY() - b.getY() );
  }

  /** Return a new vector containing a scaled by scaling factor s. */
  static Point2D scale( Point2D a, double s ) {
    return new Point2D.Double( a.getX() * s, a.getY() * s );
  }

  /** Return the magnitude of vector a. */
  static double mag( Point2D a ) {
    return Math.sqrt( a.getX() * a.getX() + a.getY() * a.getY() );
  }

  /** Return a new vector containing normalized version of a. */
  static Point2D norm( Point2D a ) {
    double m = mag( a );
    return new Point2D.Double( a.getX() / m,
                               a.getY() / m );
  }

  /** Return a ccw perpendicular vector for a. */
  static Point2D perp( Point2D a ) {
    return new Point2D.Double( -a.getY(), a.getX() );
  }

  /** Return the dot product of a and b. */
  static double dot( Point2D a, Point2D b ) {
    return a.getX() * b.getX() + a.getY() * b.getY();
  }

  /** Return the cross product of a and b. */
  static double cross( Point2D a, Point2D b ) {
    return a.getX() * b.getY() - a.getY() * b.getX();
  }

  /** One dimensional function to help compute acceleration vectors. Return an
      acceleration that can be applied to a bumper at pos and moving
      with velocity vel to get it to target.  The alim parameter puts
      a limit on the acceleration available. */
  private static double moveTo( double pos, double vel, double target,
                                double alim ) {
    // Compute how far pos has to go to hit target.
    double dist = target - pos;

    // Kill velocity if we are close enough.
    if ( Math.abs( dist ) < 0.01 )
      return clamp( -vel, -alim, alim );
    
    // How many steps, at minimum, would cover the remaining distance
    // and then stop.
    double steps = Math.ceil(( -1 + Math.sqrt(1 + 8.0 * Math.abs(dist) / alim)) 
                             / 2.0);
    if ( steps < 1 )
      steps = 1;
    
    // How much acceleration would we need to apply at each step to
    // cover dist.
    double accel = 2 * dist / ( ( steps + 1 ) * steps );
    
    // Ideally, how fast would we be going now
    double ivel = accel * steps;

    // Return the best change in velocity to get vel to ivel.
    return clamp( ivel - vel, -alim, alim );
  }

  public static int wins(int uenemy, int umine, int ugrey, int benemy, int bmine, int bgrey) {
    int uppercap = 0;
    int bottomcap = 0;
    int answer = 0;

    if(uenemy == 0){
      if(ugrey != 0) {
        uppercap = umine;
      }
      else {
        uppercap = 1;
      }
    }
    else {
        uppercap = uenemy - umine;
    }


    if(benemy == 0){
      if(bgrey != 0) {
        bottomcap = bmine;
      }
      else {
        bottomcap = 1;
      }
    }
    else {
      bottomcap = benemy - bmine;
    }

    if(uppercap > bottomcap) answer = 1;
    else if(uppercap < bottomcap) answer = 2;
    else answer = 0;

    if(answer == 1 && uppercap == 0) {
      answer = 0;
    }
    else if(answer == 2 && bottomcap == 0) answer = 0;

    return answer;
  }

  public static void main( String[] arg ) {
    // List of current sled, bumper and puck locations.
    ArrayList< Puck > plist = new ArrayList< Puck >();
    ArrayList< Bumper > blist = new ArrayList< Bumper >();
    ArrayList< Sled > slist = new ArrayList< Sled >();

    Scanner in = new Scanner( System.in );

    double minY = 900;
    double maxY = 0;
    double radius = 95;
    int sledMove = 1;
    int lastMove = 0;
    int nextMove = 0;

    int uppercap = 0;
    int bottomcap = 0;
    int lupcap = 0;
    int lbcap = 0;

    int moveCount = 0;
    double currCenterX = -1;
    double currCenterY = -1;

    // Target for each bumper.
    int[] target = { -1, -1 };

    // How much time the bumper has to pursue the target.
    int[] ttimer = { 0, 0 };

    // Keep reading states until the game ends.
    int tnum = in.nextInt();
    while ( tnum >= 0 ) {
      // Read all the pucks
      int n = in.nextInt();
      plist.clear();
      for ( int i = 0; i < n; i++ ) {
        Puck p = new Puck();
        double x = in.nextDouble();
        double y = in.nextDouble();
        p.pos = new Point2D.Double( x, y );
        x = in.nextDouble();
        y = in.nextDouble();
        p.vel = new Point2D.Double( x, y );
        p.color = in.nextInt();
        plist.add( p );
      }

      // Read all the bumpers
      n = in.nextInt();
      blist.clear();
      for ( int i = 0; i < n; i++ ) {
        Bumper b = new Bumper();
        double x = in.nextDouble();
        double y = in.nextDouble();
        b.pos = new Point2D.Double( x, y );
        x = in.nextDouble();
        y = in.nextDouble();
        b.vel = new Point2D.Double( x, y );
        blist.add( b );
      }

      // Read the sleds and their trails.
      n = in.nextInt();
      slist.clear();
      for ( int i = 0; i < n; i++ ) {
        // Read the state of the sled.
        Sled s = new Sled();
        double x = in.nextDouble();
        double y = in.nextDouble();
        s.pos = new Point2D.Double( x, y );
        s.dir = in.nextDouble();
        slist.add( s );
        
        // Just throw away the trail information.
        int ts = in.nextInt();
        for ( int j = 0; j < ts; j++ ) {
          in.nextDouble();
          in.nextDouble();
        }
      }

      // Just make each sled run toward the nearest grey puck.
      for ( int i = 0; i < 2; i++ ) {
        // Where do we try to send the pucks?
        Point2D tdest = new Point2D.Double( 100, i == 0 ? 300 : 500 );

        Bumper bumper = blist.get( i );
        if ( ttimer[ i ] <= 0 ) {
          // Find a target that's grey and close to the bumper.
          target[ i ] = -1;
          for ( int j = 0; j < plist.size(); j++ ) {
            // Pick a grey target that's close to the player and not too close
            // to the destination.
            if ( plist.get( j ).color == Const.GREY &&
                 plist.get( j ).pos.distance( tdest ) > 120 &&
                 Math.abs( plist.get( j ).pos.getX() - 400 ) < 340 &&
                 Math.abs( plist.get( j ).pos.getY() - 400 ) < 340 &&
                 ( target[ i ] < 0 ||
                   plist.get( j ).pos.distance( bumper.pos ) <
                   plist.get( target[ i ] ).pos.distance( bumper.pos ) ) )
              target[ i ] = j;
          }

          // Give a fixed number of moves to deal with the target.
          if ( target[ i ] >= 0 )
            ttimer[ i ] = 20;
        }

        if ( ttimer[ i ] > 0 ) {
          // Where's the target.
          Point2D tpos = plist.get( target[ i ] ).pos;
          
          // Split the bumper's veloicty into components toward the puck
          // and perp.
          double dist = tpos.distance( bumper.pos );
          Point2D a1 = scale( diff( tpos, bumper.pos ), 1.0 / dist );
          Point2D a2 = perp( a1 );
        
          // Represent the velocity WRT a target-centric frame.
          double v1 = dot( a1, bumper.vel );
          double v2 = dot( a2, bumper.vel );

          // compute force in this frame.
          double f1 = 0;
          double f2 = 0;

          // Direction from the puck to where we want to hit it.
          Point2D tdir = diff( tdest, tpos );

          // Should we move around the target puck?
          double dprod = dot( a1, norm( tdir ) );
          if ( dprod < 0.8 ) { 
            // Try to maintain a moderate distance to the target.
            if ( dist > 80 ) {
              f1 = ACCEL;
            } else if ( dist < 40 ) {
              f1 = -ACCEL;
            } else {
              f1 = clamp( -v1, -ACCEL, ACCEL );
            }

            // How far around do we have to around the target.
            double cdist = Math.acos( dprod ) * dist;
            // Move around the shorter way.
            if ( cross( tdir, a1 ) > 0 ) {
              f2 = ACCEL;
            } else {
              f2 = -ACCEL;
            }
          } else {
            // Consider the velocity WRT a frame that points from the target
            // puck to the destination
            a1 = norm( tdir );
            a2 = perp( a1 );

            v1 = dot( a1, bumper.vel );
            v2 = dot( a2, bumper.vel );

            // Position of the bumper WRT the target puck and a frame pointing
            // to the target's destination.
            Point2D bdisp = diff( bumper.pos, tpos );
            double p1 = dot( a1, bdisp );
            double p2 = dot( a2, bdisp );

            // How hard do we have to hit the target to get it to the
            // destination.
            double tdist = mag( tdir );

            double a = 0.5;
            double b = 0.5;
            double c = -tdist;
            
            // Number of steps the puck has to take to cover mag tdist.
            double steps = ( -b + Math.sqrt( b * b - 4 * a * c ) ) / ( 2 * a );

            // Approximate velocity to get the puck to travel to its
            // destination.
            double vel = steps * 0.7;

            // Match desired velocity in the direction we want to move the puck,
            // line up on the other axis.
            f1 = clamp( vel - v1, -ACCEL, ACCEL );
            f2 = moveTo( p2, v2, 0.0, ACCEL );

            // If we are about to hit the target, pick a new
            // one the next turn.
            if ( p1 + v1 + f1 > -13 )
              ttimer[ i ] = 1;
          }
          
          Point2D force = sum( scale( a1, f1 ), scale( a2, f2 ) );

          // Tell the game what direction we want to move this bumper.
          System.out.printf( "%.2f %.2f ", force.getX(), force.getY() );
          //System.err.printf("Force: %.2f %.2f\n", force.getX(), force.getY());

          // Count down for how long we can chase this target.
          ttimer[ i ]--;
        } else {
          // Just move to the right.
          System.out.printf( "%.2f 0.0 ", Const.BUMPER_ACCEL_LIMIT );
        }
      }


      if(slist.get(0).pos.getY() < minY){
        minY = slist.get(0).pos.getY();
      }

      if(slist.get(0).pos.getY() > maxY){
        maxY = slist.get(0).pos.getY();
      }

      double sX = slist.get(0).pos.getX();
      double sY = slist.get(0).pos.getY();
      int bgreys = 0;
      int bmine = 0;
      int benemy = 0;
      int ugreys = 0;
      int umine = 0;
      int uenemy = 0;
      int lupmine = 0;
      int lupgreys = 0;
      int lupenemy = 0;
      int lbmine = 0;
      int lbgreys = 0;
      int lbenemy = 0;

      int rotating = 0;


      if ( moveCount - lastMove <= 40 && bottomcap >= uppercap && bottomcap >= lupcap && bottomcap >= lbcap && bottomcap > 0) {
        if(moveCount - lastMove == 1){
          currCenterX = slist.get(0).pos.getX();
          currCenterY = slist.get(1).pos.getY();
        }
        rotating = 1;

        System.out.printf( "%.6f\n", Math.PI * 2.0 / 40 );
      } else if(moveCount - lastMove <= 40 && uppercap >= bottomcap && uppercap >= lupcap && uppercap >= lbcap && uppercap > 0){
        if(moveCount - lastMove == 1){
          currCenterX = slist.get(0).pos.getX();
          currCenterY = slist.get(1).pos.getY();
        }

        rotating = 1;
        System.out.printf( "%.6f\n", -Math.PI * 2.0 / 40 );
      }
      else if(moveCount - lastMove < 4 && lupcap >= bottomcap && lupcap >= uppercap && lupcap > 0){
        System.out.println("0.0");
      }
      else if(moveCount - lastMove < 4 && lbcap >= bottomcap && lbcap >= uppercap && lbcap > 0){
        System.out.println("0.0");
      }
      else{
        lastMove = moveCount;

        for(int i = 0; i < plist.size(); i++){
          double pX = plist.get(i).pos.getX();
          double pY = plist.get(i).pos.getY();
          double dX = pX - sX;
          double dY = pY - sY;
          double a1 = dY - radius;
          double a2 = dY + radius;
          double lC = dX + 45;

          int color = plist.get(i).color;
          if(dX*dX + a1*a1 < radius*radius){
            if(color == Const.GREY) bgreys++;
            else if(color == Const.BLUE) benemy++;
            else bmine++;
          }

          if(dX*dX + a2*a2 < radius*radius){
            if(color == Const.GREY) ugreys++;
            else if(color == Const.BLUE) uenemy++;
            else umine++;
          }

          if(lC*lC + a2*a2 < radius*radius){
            if(color == Const.GREY) lupgreys++;
            else if(color == Const.BLUE) lupenemy++;
            else lupmine++;
          }

          if(lC*lC + a1*a1 < radius*radius){
            if(color == Const.GREY) lbgreys++;
            else if(color == Const.BLUE) lbenemy++;
            else lbmine++;
          }

        }
        //System.err.printf("%d %d %d ", umine, ugreys, uenemy);
        //System.err.printf("%d %d %d\n", bmine, bgreys, benemy);

        if(uenemy == 0){
          if(ugreys != 0 && umine != 0) {
            uppercap = ugreys;
          }
          else {
            uppercap = 0;
          }
        }
        else {
          if(umine != 0) uppercap = uenemy - umine;
          else uppercap = -ugreys;
        }

        if(benemy == 0){
          if(bgreys != 0 && bmine != 0) {
            bottomcap = bgreys;
          }
          else {
            bottomcap = 0;
          }
        }
        else {
          if(bmine != 0) bottomcap = benemy - bmine;
          else bottomcap = -bgreys;
        }

        if(lupenemy == 0){
          if(lupgreys != 0 && lupmine != 0) {
            lupcap = lupgreys;
          }
          else {
            lupcap = 0;
          }
        }
        else {
          if(lupmine != 0) lupcap = lupenemy - lupmine;
          else lupcap = -lupgreys;
        }

        if(lbenemy == 0){
          if(lbgreys != 0 && lbmine != 0) {
            lbcap = lbgreys;
          }
          else {
            lbcap = 0;
          }
        }
        else {
          if(lbmine != 0) lbcap = lbenemy - lbmine;
          else lbcap = -lbgreys;
        }

        //System.err.printf("Bottomcap is %d ", bottomcap);
        //System.err.printf("; Uppercap is %d ", uppercap);
        //System.err.printf("; Lupcap is %d ", lupcap);
        //System.err.printf("; Lbcap is %d \n", lbcap);

        System.out.println("0.0");
      }

      /*
      if(currCenterX != -1 && currCenterY != -1) {
        for (int i = 0; i < plist.size(); i++) {
          double pX = plist.get(i).pos.getX();
          double pY = plist.get(i).pos.getY();
          double dX = pX - currCenterX;
          double dY = pY - currCenterY;
          double a1 = dY - radius;
          double a2 = dY + radius;
          double lC = dX + 45;

          int color = plist.get(i).color;
          if (dX * dX + a1 * a1 < radius * radius) {
            if (color == Const.GREY) bgreys++;
            else if (color == Const.BLUE) benemy++;
            else bmine++;
          }

          if (dX * dX + a2 * a2 < radius * radius) {
            if (color == Const.GREY) ugreys++;
            else if (color == Const.BLUE) uenemy++;
            else umine++;
          }

          if (lC * lC + a2 * a2 < radius * radius) {
            if (color == Const.GREY) lupgreys++;
            else if (color == Const.BLUE) lupenemy++;
            else lupmine++;
          }

          if (lC * lC + a1 * a1 < radius * radius) {
            if (color == Const.GREY) lbgreys++;
            else if (color == Const.BLUE) lbenemy++;
            else lbmine++;
          }

        }
        //System.err.printf("%d %d %d ", umine, ugreys, uenemy);
        //System.err.printf("%d %d %d\n", bmine, bgreys, benemy);

        if (uenemy == 0) {
          if (ugreys != 0 && umine != 0) {
            uppercap = ugreys;
          } else {
            uppercap = 0;
          }
        } else {
          if (umine != 0) uppercap = uenemy - umine;
          else uppercap = -ugreys;
        }

        if (benemy == 0) {
          if (bgreys != 0 && bmine != 0) {
            bottomcap = bgreys;
          } else {
            bottomcap = 0;
          }
        } else {
          if (bmine != 0) bottomcap = benemy - bmine;
          else bottomcap = -bgreys;
        }

        if (lupenemy == 0) {
          if (lupgreys != 0 && lupmine != 0) {
            lupcap = lupgreys;
          } else {
            lupcap = 0;
          }
        } else {
          if (lupmine != 0) lupcap = lupenemy - lupmine;
          else lupcap = -lupgreys;
        }

        if (lbenemy == 0) {
          if (lbgreys != 0 && lbmine != 0) {
            lbcap = lbgreys;
          } else {
            lbcap = 0;
          }
        } else {
          if (lbmine != 0) lbcap = lbenemy - lbmine;
          else lbcap = -lbgreys;
        }
      }
      */
     // System.err.printf("%.2f %.2f\n", currCenterX, currCenterY);

      tnum = in.nextInt();
      moveCount++;

    }
    }
  }
