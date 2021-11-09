package com.example.tetris

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.Observer
import com.example.tetris.view.*
import com.example.tetris.viewmodel.TetrisViewModel
import com.example.tetris.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
  private lateinit var mDetector: GestureDetectorCompat
  private val userViewModel: UserViewModel by viewModel()
  private val tetrisViewModel: TetrisViewModel by viewModel()

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_main)
    mDetector = GestureDetectorCompat(this, MyGestureListener(tetrisViewModel))

    if (savedInstanceState == null) {
      initFragments()
    }

    tetrisViewModel.startGame()

    Firebase.dynamicLinks
      .getDynamicLink(intent)
      .addOnSuccessListener(this) {
        userViewModel.emailAddress.value?.let { it1 -> signingWithEmail(it1) }
      }
      .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
  }

  override fun onRestart() {
    super.onRestart()
    tetrisViewModel.startTimer()
  }

  override fun onStop() {
    super.onStop()
    tetrisViewModel.deleteTimer()
  }

  override fun onTouchEvent(e: MotionEvent): Boolean {
    mDetector.onTouchEvent(e)
    return super.onTouchEvent(e)
  }

  private fun initFragments() {
    val fragmentManager = supportFragmentManager
    val fragmentTransaction = fragmentManager.beginTransaction()

    val tetrisFragment = TetrisFragment()
    fragmentTransaction.add(R.id.fragment_container, tetrisFragment, "Tetris")

    val gameoverFragment = GameoverFragment()
    fragmentTransaction.add(R.id.fragment_container, gameoverFragment, "Gameover")
    fragmentTransaction.detach(gameoverFragment)

    val rankingFragment = RankingFragment()
    fragmentTransaction.add(R.id.fragment_container, rankingFragment, "Ranking")
    fragmentTransaction.detach(rankingFragment)

    val rankingSubmitFormFragment = RankingSubmitFormFragment()
    fragmentTransaction.add(
      R.id.fragment_container,
      rankingSubmitFormFragment,
      "RankingSubmitForm"
    )
    fragmentTransaction.detach(rankingSubmitFormFragment)

    val authenticationFragment = AuthenticationFragment()
    fragmentTransaction.add(R.id.fragment_container, authenticationFragment, "Authentication")
    fragmentTransaction.detach(authenticationFragment)

    fragmentTransaction.commit()
  }

  private fun signingWithEmail(data:String) {
    Log.d("aaaa",data)
    val auth = Firebase.auth
    val intent = intent
    val emailLink = intent.data.toString()

    if (auth.isSignInWithEmailLink(emailLink)) {
      if (data !== "") {
        auth.signInWithEmailLink(data, emailLink)
          .addOnCompleteListener { task ->
            if (task.isSuccessful) {
              userViewModel.isLogging = true
              Toast.makeText(applicationContext, "ログインしました", LENGTH_SHORT).show()
            } else {
              Toast.makeText(applicationContext, "ログイン出来ませんでした", LENGTH_SHORT).show()
            }
          }
      } else {
        Log.d("bbbb",data)
        Toast.makeText(applicationContext, "ログイン出来ませんでした", LENGTH_SHORT).show()
      }
    }
  }

  private class MyGestureListener(val tetrisViewModel: TetrisViewModel) :
    GestureDetector.SimpleOnGestureListener() {
    override fun onFling(
      e1: MotionEvent,
      e2: MotionEvent,
      velocityX: Float,
      velocityY: Float
    ): Boolean {
      val calculateX = e1.x - e2.x
      if (calculateX < 0) {
        tetrisViewModel.flickToRight()
      } else {
        tetrisViewModel.flickToLeft()
      }
      return super.onFling(e1, e2, velocityX, velocityY)
    }

    override fun onDown(e: MotionEvent?): Boolean {
      return true
    }

    override fun onLongPress(e: MotionEvent) {
      tetrisViewModel.fallDownBlock()
      super.onLongPress(e)
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
      tetrisViewModel.rotate()
      return super.onDoubleTap(e)
    }
  }
}
