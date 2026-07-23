package com.jn.dropbit.di

import android.content.Context
import com.jn.dropbit.di.modules.DataModule
import com.jn.dropbit.di.modules.DomainModule
import com.jn.dropbit.di.modules.ViewModelModule

class AppContainer(context: Context) {
    val dataComponent: DataComponent = DataModule(context)
    val domainComponent: DomainComponent = DomainModule(dataComponent as DataModule)
    private val viewModelModule = ViewModelModule(dataComponent, domainComponent)

    val viewModelFactory = viewModelModule.viewModelFactory
    val soundManager = dataComponent.soundManager
}
