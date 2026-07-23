package com.jn.dropbit.di.modules

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.jn.dropbit.billing.BillingManager
import com.jn.dropbit.data.repository.GameRepositoryImpl
import com.jn.dropbit.di.DataComponent
import com.jn.dropbit.domain.repository.IGameRepository
import com.jn.dropbit.utils.SoundManager

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataModule(private val context: Context) : DataComponent {
    override val gameRepository: IGameRepository by lazy {
        GameRepositoryImpl(context.dataStore)
    }

    override val billingManager: BillingManager by lazy {
        BillingManager(context)
    }

    override val soundManager: SoundManager by lazy {
        SoundManager(context, gameRepository)
    }
}
