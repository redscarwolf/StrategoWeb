package controllers;

import akka.actor.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.htwg.stratego.StrategoModule;

import de.htwg.stratego.controller.IMultiDeviceStrategoController;
import de.htwg.stratego.controller.ISingelDeviceStrategoController;
import de.htwg.stratego.controller.IStrategoController;

public class GameActor extends UntypedActor {

    public static Props props(ActorRef playerOne, ActorRef playerTwo) {
        return Props.create(GameActor.class, playerOne, playerTwo);
    }

    private ActorRef playerOne;
    private ActorRef playerTwo;

    private IMultiDeviceStrategoController strategoController;

    public GameActor(ActorRef playerOne, ActorRef playerTwo) {
        Injector injector = Guice.createInjector(new StrategoModule());
		strategoController = injector.getInstance(IMultiDeviceStrategoController.class);
		
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
		
		self().tell(new GameProtocol.Add(2, 2, 0), self());
    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof GameProtocol.Add) {
            GameProtocol.Add add = (GameProtocol.Add) message;
            strategoController.add(add.column, add.row, add.rank, strategoController.getPlayerOne());
            playerOne.tell(new GameProtocol.Refresh(strategoController.toJson(strategoController.getPlayerOne())), self());
            playerTwo.tell(new GameProtocol.Refresh(strategoController.toJson(strategoController.getPlayerTwo())), self());
        }
    }

}