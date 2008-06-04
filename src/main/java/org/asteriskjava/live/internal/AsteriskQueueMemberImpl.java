/*
 *  Copyright 2004-2006 Stefan Reuter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.asteriskjava.live.internal;

import org.asteriskjava.live.*;
import org.asteriskjava.manager.response.ManagerResponse;
import org.asteriskjava.manager.response.ManagerError;
import org.asteriskjava.manager.action.AbsoluteTimeoutAction;
import org.asteriskjava.manager.action.QueuePenaltyAction;

/**
 * Default implementation of a queue member.
 *
 * @author <a href="mailto:patrick.breucking{@nospam}gonicus.de">Patrick Breucking</a>
 * @version $Id$
 * @see AsteriskQueueMember
 * @since 0.3.1
 */
class AsteriskQueueMemberImpl extends AbstractLiveObject implements AsteriskQueueMember
{
    private AsteriskQueue queue;
    private QueueMemberState state;
    private String location;
    private Integer penalty;
    private boolean paused;

    /**
     * Creates a new queue member.
     *
     * @param server   server this channel belongs to.
     * @param queue    queue this member is registered to.
     * @param location location of member.
     * @param state    state of this member.
     * @param penalty  penalty of this member.
     */
    AsteriskQueueMemberImpl(final AsteriskServerImpl server,
                            final AsteriskQueueImpl queue, String location,
                            QueueMemberState state, boolean paused, 
                            Integer penalty)
    {
        super(server);
        this.queue = queue;
        this.location = location;
        this.state = state;
        this.penalty = penalty;
        this.paused = paused;
    }

    public AsteriskQueue getQueue()
    {
        return queue;
    }

    public String getLocation()
    {
        return location;
    }

    public QueueMemberState getState()
    {
        return state;
    }
    
    public boolean getPaused() {
        return paused;
    }

    public Integer getPenalty()
    {
        return penalty;
    }

    public void setPenalty(int penalty) throws IllegalArgumentException, ManagerCommunicationException, InvalidPenaltyException
    {
        ManagerResponse response;

        if (penalty < 0)
        {
            throw new IllegalArgumentException("Penalty must not be negative");
        }

        response = server.sendAction(new QueuePenaltyAction(location, penalty, queue.getName()));
        if (response instanceof ManagerError)
        {
            throw new InvalidPenaltyException("Unable to set penalty for '" + location + "' on '" +
                    queue.getName() + "': " + response.getMessage());
        }
    }

    @Override
    public String toString()
    {
        final StringBuffer sb;

        sb = new StringBuffer("AsteriskQueueMember[");
        sb.append("location='").append(location).append("'");
        sb.append("state='").append(state).append("'");
        sb.append("paused='").append(paused).append("'");
        sb.append("queue='").append(queue.getName()).append("'");
        sb.append("systemHashcode=").append(System.identityHashCode(this));
        sb.append("]");

        return sb.toString();
    }

    synchronized void stateChanged(QueueMemberState state)
    {
        QueueMemberState oldState = this.state;
        this.state = state;
        firePropertyChange(PROPERTY_STATE, oldState, state);
    }

    synchronized void penaltyChanged(Integer penalty)
    {
        Integer oldPenalty = this.penalty;
        this.penalty = penalty;
        firePropertyChange(PROPERTY_PENALTY, oldPenalty, penalty);
    }
    
    synchronized void pausedChanged(boolean paused)
    {
        boolean oldPaused = this.paused;
        this.paused = paused;
        firePropertyChange(PROPERTY_PAUSED, oldPaused, paused);
    }
}
