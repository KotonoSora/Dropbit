package com.jn.dropbit.di.modules

import com.jn.dropbit.di.DomainComponent
import com.jn.dropbit.domain.engine.GameEngine
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

class DomainModule(private val dataModule: DataModule) : DomainComponent {
    override val saveHighScoreUseCase by lazy { SaveHighScoreUseCase(dataModule.gameRepository) }
    override val getPlayerSkinUseCase by lazy { GetPlayerSkinUseCase(dataModule.gameRepository) }
    override val savePlayerSkinUseCase by lazy { SavePlayerSkinUseCase(dataModule.gameRepository) }
    override val getCoinsUseCase by lazy { GetCoinsUseCase(dataModule.gameRepository) }
    override val saveCoinsUseCase by lazy { SaveCoinsUseCase(dataModule.gameRepository) }
    override val getHistoryUseCase by lazy { GetHistoryUseCase(dataModule.gameRepository) }
    override val saveHistoryUseCase by lazy { SaveHistoryUseCase(dataModule.gameRepository) }
    override val getSettingsUseCase by lazy { GetSettingsUseCase(dataModule.gameRepository) }
    override val saveSettingsUseCase by lazy { SaveSettingsUseCase(dataModule.gameRepository) }
    override val processGameOverUseCase by lazy {
        ProcessGameOverUseCase(
            saveHighScoreUseCase,
            getCoinsUseCase,
            saveCoinsUseCase,
            saveHistoryUseCase
        )
    }
    override val gameEngine by lazy { GameEngine() }
}
