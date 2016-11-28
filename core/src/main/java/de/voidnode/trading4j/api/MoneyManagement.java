package de.voidnode.trading4j.api;

import de.voidnode.trading4j.domain.Volume;

/**
 * Manages the money of a trading account by providing the {@link Volume} an {@link ExpertAdvisor} is allowed to invest
 * for a single trade.
 * 
 * @author Raik Bieniek
 */
public interface MoneyManagement extends AccountBalanceManager, VolumeLender {

}
