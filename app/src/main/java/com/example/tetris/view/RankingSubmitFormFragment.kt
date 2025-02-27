package com.example.tetris.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.tetris.databinding.RankingSubmitFormBinding
import com.example.tetris.misc.Score
import com.example.tetris.model.Repository
import com.example.tetris.viewmodel.TetrisViewModel
import com.example.tetris.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.ranking_submit_form.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RankingSubmitFormFragment : Fragment() {
  private val tetrisViewModel: TetrisViewModel by sharedViewModel()
  private val userViewModel: UserViewModel by sharedViewModel()
  private val repository = Repository()
  private var _binding: RankingSubmitFormBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = RankingSubmitFormBinding.inflate(inflater, container, false)
    binding.viewModel = tetrisViewModel
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    submitButton.setOnClickListener {
      val userName = editTextTextPersonName.text.toString()
      val score = Score(userName,tetrisViewModel.score.value)
      repository.setScore(score)
      userViewModel.submittedScore.value = true
    }

    backButton.setOnClickListener{
      val rankingSubmitFormFragment = parentFragmentManager.findFragmentByTag("RankingSubmitForm")
      val gameoverFragment = parentFragmentManager.findFragmentByTag("Gameover")
      if(rankingSubmitFormFragment != null && gameoverFragment != null) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.detach(rankingSubmitFormFragment)
        transaction.attach(gameoverFragment)
        transaction.commit()
      }
    }

    val submittedScoreObserver = Observer<Boolean> { isSubmittedScore ->
     if (isSubmittedScore == true){
       submitButton.isClickable = false
       submitButton.text = "登録済み"
     }
    }
    userViewModel.submittedScore.observe(viewLifecycleOwner, submittedScoreObserver)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}