package MyBot;

import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import ai.*;

import java.util.List;
import java.util.Random;

import rts.GameState;
import rts.PlayerAction;
import rts.units.UnitType;
import rts.units.UnitTypeTable;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Oliver King
 */
public class MyBot extends AIWithComputationBudget {
    UnitTypeTable m_utt = null;
    Random r = new Random();
    
    private int maxCycles; 
    private int minSearchValue;
    private int maxSearchValue;
    UnitType workerType;
    UnitType baseType;
    UnitType barracksType;
    UnitType rangedType; 
    // Unused UnitTypes are left in incase they need to be implemented in near future
    AI policy;

    public MyBot(UnitTypeTable utt) {
        super(-1, -1);
        m_utt = utt;
        
        maxCycles = 1000; 
        minSearchValue = 0; 
        maxSearchValue = 100;
        policy = new RandomAI();
        // Currently uses a random Policy but this may be updated in the future
    }

    @Override
    public void reset() {
    }
    
    @Override
    public void reset(UnitTypeTable m_utt){
        
        workerType = m_utt.getUnitType("Worker");
        baseType = m_utt.getUnitType("Base");
        barracksType = m_utt.getUnitType("Barracks");
        rangedType = m_utt.getUnitType("Ranged");     
        
        //Sets up the Unit type table for all the units I am planning on using
    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        
       int best = 0;
       PlayerAction bestAct = null;
       for ( int i = 0; i < 100; ++i ) { // This is how many times a random action is generated 
           var gsc = gs.clone();
           PlayerAction act = policy.getAction(player, gsc);// Gets the random action
           gsc.issue(act);
           
           int myScore = binarySearch(player, gsc, 1000, -1000);
           // Calls the Search algorithm. The minvalue and maxvalue can be edited to tinker with the bot
           if ( best > myScore || bestAct == null ) {
               best = myScore;
               bestAct = act;
               // This little section just picks the best move
           }
       }
       
       return bestAct;
    }
    public int  binarySearch(int player, GameState gs, int minValue, int maxValue )throws Exception{
        if (maxCycles <= 0 || minValue >= maxValue) {
            return minValue; // Return the current minimum value
        }
        int midValue = (minValue + maxValue) / 2; // Calculate the middle value
        int score = evaluateState(gs);
        GameState copy = gs.clone();
        AI ai = new RandomBiasedAI();  //This shouuld be set the the AI that the bot is going to play against  
        copy.setTime(0);
        ai.reset();
        int cycles = 0;
        while (!copy.gameover() && cycles < maxCycles) {
            PlayerAction pa = ai.getAction(player == 0 ? 1 : 0, copy);// Switches the player between the bot and the opponent AI
            copy.issue(pa);
            cycles++;
        }
        if (score >= midValue) {
            // The score is greater than or equal to the middle value, so search the upper half
            return binarySearch(player, gs, midValue + 1, maxValue);
        } else {
            // The score is less than the middle value, so search the lower half
            return binarySearch(player, gs, minValue, midValue - 1);
        }
    }
    
    @Override
    public AI clone() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    // The 2 functions above are required for the bot to work (I think)
    
    public int evaluateState(GameState gs){
    return r.nextInt(maxSearchValue - minSearchValue + 1) + minSearchValue;
    // Evaluates the gamestate
    }
}
