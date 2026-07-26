package com.jn.dropbit.di

import com.jn.dropbit.billing.BillingManager
import com.jn.dropbit.domain.engine.GameEngine
import com.jn.dropbit.domain.repository.IGameRepository
import com.jn.dropbit.domain.usecase.GetCoinsUseCase
import com.jn.dropbit.domain.usecase.GetHistoryUseCase
import com.jn.dropbit.domain.usecase.GetPlayerSkinUseCase
import com.jn.dropbit.domain.usecase.GetSettingsUseCase
import com.jn.dropbit.domain.usecase.ProcessGameOverUseCase
import com.jn.dropbit.domain.usecase.SaveCoinsUseCase
import com.jn.dropbit.domain.usecase.SaveHighScoreUseCase
import com.jn.dropbit.domain.usecase.SaveHistoryUseCase
import com.jn.dropbit.domain.usecase.SavePlayerSkinUseCase
import com.jn.dropbit.domain.usecase.SaveSettingsUseCase
import com.jn.dropbit.utils.SoundManager

interface DataComponent {
    val gameRepository: IGameRepository
    val billingManager: BillingManager
    val soundManager: SoundManager
}

interface DomainComponent {
    val saveHighScoreUseCase: SaveHighScoreUseCase
    val getPlayerSkinUseCase: GetPlayerSkinUseCase
    val savePlayerSkinUseCase: SavePlayerSkinUseCase
    val getCoinsUseCase: GetCoinsUseCase
    val saveCoinsUseCase: SaveCoinsUseCase
    val getHistoryUseCase: GetHistoryUseCase
    val saveHistoryUseCase: SaveHistoryUseCase
    val getSettingsUseCase: GetSettingsUseCase
    val saveSettingsUseCase: SaveSettingsUseCase
    val processGameOverUseCase: ProcessGameOverUseCase
    val gameEngine: GameEngine
}
