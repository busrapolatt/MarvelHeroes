/*
 * Designed and developed by 2020 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.marvelheroes.viewmodel

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.skydoves.marvelheroes.MainCoroutinesRule
import com.skydoves.marvelheroes.model.Poster
import com.skydoves.marvelheroes.network.MarvelClient
import com.skydoves.marvelheroes.network.MarvelService
import com.skydoves.marvelheroes.persistence.PosterDao
import com.skydoves.marvelheroes.repository.MainRepository
import com.skydoves.marvelheroes.utils.MockTestUtil
import com.skydoves.marvelheroes.view.ui.main.MainViewModel
import com.skydoves.sandwich.ResponseDataSource
import com.skydoves.sandwich.disposables.CompositeDisposable
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.seconds

class MainViewModelTest {

  private lateinit var viewModel: MainViewModel
  private lateinit var mainRepository: MainRepository
  private val marvelService: MarvelService = mock()
  private val marvelClient: MarvelClient = MarvelClient(marvelService)
  private val posterDao: PosterDao = mock()
  private val dataSource: ResponseDataSource<List<Poster>> = mock()

  @get:Rule
  var coroutinesRule = MainCoroutinesRule()

  @Before
  fun setup() {
    mainRepository = MainRepository(marvelClient, dataSource, posterDao)
    viewModel = MainViewModel(mainRepository)
  }

  @Test
  fun fetchMarvelPosterListTest() = runBlocking {
    val mockData = MockTestUtil.mockPosterList()
    whenever(posterDao.getPosterList()).thenReturn(mockData)

    val compositeDisposable = CompositeDisposable()
    mainRepository.loadMarvelPosters(
      compositeDisposable,
      viewModel.viewModelScope
    ) {
    }.test(2.seconds) {
      val item = expectItem()
      Assert.assertEquals(item, mockData)
      expectComplete()
    }
  }
}
