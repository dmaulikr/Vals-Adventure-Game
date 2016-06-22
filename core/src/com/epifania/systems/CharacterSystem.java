package com.epifania.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.epifania.components.CharacterComponent;
import com.epifania.components.TransformComponent;
import com.epifania.components.Val_Component;
import com.epifania.utils.ConversationManager;
import com.epifania.utils.InputHandler;

/**
 * Created by juan on 5/28/16.
 */
public class CharacterSystem extends IteratingSystem {
    //TODO I can use AI extension with states machines

    private static final String tag = "Character System";
    private static final float dstXM = 2.5f;
    private static final float dstYM = 4;
    private static final float dstYm = -2;

    private States state = States.WAITING_IN;
    private Entity val;

    public InputHandler inputHandler;
    public ConversationManager manager;
    private boolean isSecondary = false;

    public CharacterSystem() {
        super(Family.all(CharacterComponent.class,TransformComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        for(Entity val : getEngine().getEntitiesFor(Family.all(Val_Component.class,TransformComponent.class).get())) {
            this.val = val;
            CharacterComponent characterComponent = entity.getComponent(CharacterComponent.class);
            switch (state) {
                case WAITING_IN:
                    String conversationID = characterComponent.conversationIDs.get(characterComponent.current);
                    Array<String> valKeys = val.getComponent(Val_Component.class).conversationKeys;

                    if (isEntityClose(entity, val)) {
                        if (manager.matches(conversationID, valKeys)) {
                            state = States.CONVERSATING;
                            isSecondary = false;
                            getEngine().getSystem(Val_System.class).setMove(val,false);
                            inputHandler.setActive(false);
                            manager.startConversation(conversationID);
                        }else if(manager.hasSecondary(conversationID)){
                            state = States.CONVERSATING;
                            isSecondary = true;
                            getEngine().getSystem(Val_System.class).setMove(val,false);
                            inputHandler.setActive(false);
                            manager.startConversation(manager.getSecondaryOf(conversationID));
                        }
                    }
                    break;
                case CONVERSATING:
                    if (manager.conversationEnded()) {
                        inputHandler.setActive(true);
                        getEngine().getSystem(Val_System.class).canMove = true;
                        state = States.WATING_OUT;
                    }
                    break;
                case WATING_OUT:
                    if (!isEntityClose(entity, val)) {
                        if(characterComponent.current+1<characterComponent.conversationIDs.size) {
                            if(!isSecondary) {
                                conversationID = characterComponent.conversationIDs.get(characterComponent.current);
                                val.getComponent(Val_Component.class).conversationKeys.add(conversationID);
                                characterComponent.current++;
                            }
                            manager.line=0;
                            state = States.WAITING_IN;
                        }else{
                            if(!isSecondary) {
                                state = States.DO_NOTHING;
                            }else{
                                manager.line=0;
                                state = States.WAITING_IN;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private boolean isEntityClose(Entity entity, Entity other){
        Vector3 entityPosition = entity.getComponent(TransformComponent.class).pos;
        Vector3 otherPosition = other.getComponent(TransformComponent.class).pos;

        float dstX = Math.abs(entityPosition.x - otherPosition.x);
        float dstY = entityPosition.y - otherPosition.y;

        if(dstX < dstXM && (dstY<dstYM && dstY>dstYm))
            return  true;

        return false;
    }

    private enum States{
        WAITING_IN,CONVERSATING,WATING_OUT,DO_NOTHING
    }
}
