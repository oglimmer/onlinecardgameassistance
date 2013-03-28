package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.oglimmer.bcg.logic.GameException;

public enum ActionFactory {
	INSTANCE;

	private List<Action> def = new ArrayList<>();
	private Map<String, Action> cache = new HashMap<>();

	private ActionFactory() {
		def.add(new InitAction());
		def.add(new TakeCardIntoHandAction());
		def.add(new PlayCardOnTableAction());
		def.add(new MoveCardAction());
		def.add(new RotateCardAction());
		def.add(new TakeCardIntoHandAction());
		def.add(new TakeCardPlayOnTableAction());
		def.add(new FlipCardAction());
		def.add(new DiscardAction());
		def.add(new PreinitAction());
		def.add(new PingAction());
		def.add(new ModCounterAction());
		def.add(new ReturnToDeckAction());
		def.add(new ShuffleAction());
		def.add(new ToggleHighlightAction());
	}

	public Action getAction(String actionId) {
		Action action = cache.get(actionId);
		if (action == null) {
			action = fillCacheFromDef(actionId);
			if (action == null) {
				throw new GameException("No game action with id = " + actionId);
			}
		}
		return action;
	}

	private Action fillCacheFromDef(String actionId) {
		Action action = null;
		for (Action actionDef : def) {
			String name = getActionName(actionDef);
			if (name.equals(actionId)) {
				synchronized (cache) {
					cache.put(name, actionDef);
				}
				action = actionDef;
				break;
			}
		}
		return action;
	}

	private String getActionName(Action actionDef) {
		String name = actionDef.getClass().getSimpleName();
		name = name.substring(0, 1).toLowerCase() + name.substring(1);
		name = name.substring(0, name.indexOf("Action"));
		return name;
	}
}
