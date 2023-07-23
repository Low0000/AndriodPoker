package com.example.pokergame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageView card1, card2, card3, card4, centerLeadCard;
    private ImageView cardOpen1, cardOpen2, cardOpen3, cardOpen4;
    private TextView textViewPlayer1, textViewPlayer2, textViewPlayer3, textViewPlayer4, textViewLeadCard;
    private List<TextView> textViewPlayerList;
    private List<ImageView> cardImgList, cardImgOpenList;
    private PopupWindow popupWindow;
    private PopupWindow currentPopupWindow;

    private List<String> cardStrings;
    private List<Integer> imageList;
    private List<Card> cardRealIndex;
    private Map<Integer, Card> centerCards;

    private int activeCard;
    private int currentPlayerIndex = 0;
    private int trickNumber;
    private int round;

    private boolean isCurrentPlayerTurn = true;

    private List<Card> deck;
    private List<Card> centerDeck;
    private List<Player> players;


    private Card leadCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        getSupportActionBar().hide();

        initGame();
        highlightPlayer(currentPlayerIndex);

        Button restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        for (int i = 0; i < cardImgList.size(); i++) {
            final int playerIndex = i; // Save the player index for the click listener

            cardImgList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Check if it's the current player's turn
                    if (currentPlayerIndex == playerIndex) {
                        handleCardClick(cardImgList.get(playerIndex));

                        // Disable the turn for the current player after clicking their card

                        // Pass the turn to the next player
                    } else {
                        showToast("Not Your Turn, Player" + (currentPlayerIndex+1) + " turn!");
                    }
                }
            });
        }


    }

    private void initGame() {
        trickNumber = 1;
        round = 1;

        deck = createDeck();
        System.out.println(deck.toString());
        centerDeck = new ArrayList<>();
        centerCards = new LinkedHashMap<>();

        Collections.shuffle(deck);

        players = new ArrayList<>();
        for(int i = 1; i <= 4; i++) {
            players.add(new Player("Player " + i));
        }

        cardRealIndex = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            cardRealIndex.add(new Card(Suit.BLANK,Rank.BLANK));
        }



        dealCards();

        currentPlayerIndex = determineFirstPlayerIndex();

        centerLeadCard = findViewById(R.id.centerCardImageView);
        int imageResId = getResources().getIdentifier(leadCard.toString(), "drawable", getPackageName());
        // Check if the resource identifier is valid (not 0)
        if (imageResId != 0) {
            // Set the image resource to the centerCardImageView
            centerLeadCard.setImageResource(imageResId);

            // Make the centerCardImageView visible
            centerLeadCard.setVisibility(View.VISIBLE);
        }

        card1 = findViewById(R.id.imageViewLeft);
        card2 = findViewById(R.id.imageViewTop);
        card3 = findViewById(R.id.imageViewRight);
        card4 = findViewById(R.id.imageViewBottom);

        cardOpen1 = findViewById(R.id.imageViewLeftBack);
        cardOpen2 = findViewById(R.id.imageViewTopBack);
        cardOpen3 = findViewById(R.id.imageViewRightBack);
        cardOpen4 = findViewById(R.id.imageViewBottomBack);

        textViewPlayer1 = findViewById(R.id.textViewPlayer1);
        textViewPlayer2 = findViewById(R.id.textViewPlayer2);
        textViewPlayer3 = findViewById(R.id.textViewPlayer3);
        textViewPlayer4 = findViewById(R.id.textViewPlayer4);
        textViewLeadCard = findViewById(R.id.textViewLeadCard);


        cardImgList = new ArrayList<>();
        cardImgOpenList = new ArrayList<>();
        textViewPlayerList = new ArrayList<>();

        cardImgList.add(card1);
        cardImgList.add(card2);
        cardImgList.add(card3);
        cardImgList.add(card4);

        cardImgOpenList.add(cardOpen1);
        cardImgOpenList.add(cardOpen2);
        cardImgOpenList.add(cardOpen3);
        cardImgOpenList.add(cardOpen4);

        textViewPlayerList.add(textViewPlayer1);
        textViewPlayerList.add(textViewPlayer2);
        textViewPlayerList.add(textViewPlayer3);
        textViewPlayerList.add(textViewPlayer4);

        highlightPlayer(currentPlayerIndex);

    }

    private List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        for(int i = 1; i < Suit.values().length; i++) {
            for(int j = 1; j < Rank.values().length; j++) {
                Suit suit = Suit.values()[i];
                Rank rank = Rank.values()[j];
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }

    private void dealCards() {
        int numPlayers = players.size();
        int cardsPerPlayer = 7;

        Collections.shuffle(deck);

        leadCard = deck.get(0);

        int currentCardIndex = 1;

        for(int i = 0;i < cardsPerPlayer; i++) {
            for(int j = 0; j < numPlayers; j++) {
                Player player = players.get(j);
                Card card = deck.get(currentCardIndex);
                player.addToHand(card);
                currentCardIndex++;
            }
        }

        for(int i = currentCardIndex; i < deck.size(); i++) {
            centerDeck.add(deck.get(i));
        }
    }

    private int determineFirstPlayerIndex() {
        Card leadcard = leadCard;

        // Determine the index of the first player based on the lead card
        if (leadcard.getRank() == Rank.ACE || leadcard.getRank() == Rank.FIVE ||
                leadcard.getRank() == Rank.NINE || leadcard.getRank() == Rank.KING) {
            return 0; // Player 1
        } else if (leadcard.getRank() == Rank.TWO || leadcard.getRank() == Rank.SIX ||
                leadcard.getRank() == Rank.TEN) {
            return 1; // Player 2
        } else if (leadcard.getRank() == Rank.THREE || leadcard.getRank() == Rank.SEVEN ||
                leadcard.getRank() == Rank.JACK) {
            return 2; // Player 3
        } else if (leadcard.getRank() == Rank.FOUR || leadcard.getRank() == Rank.EIGHT ||
                leadcard.getRank() == Rank.QUEEN) {
            return 3; // Player 4
        }
        return 0; // Default to player 1 if no matching rank is found
    }

    private void handleCardClick(ImageView cardImageView) {
        activeCard = cardImgList.indexOf(cardImageView);

        if(currentPopupWindow != null && currentPopupWindow.isShowing()) {
            currentPopupWindow.dismiss();
        }

        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

        popupWindow = new PopupWindow(popupView, 1000, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set system UI visibility flags
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        popupView.setSystemUiVisibility(uiOptions);

        // Populate the pop-up window with card images
        populatePopupWindow(popupView, activeCard);

        LinearLayout cardContainer = popupView.findViewById(R.id.cardContainer);



        // Set other properties of the pop-up window
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // Code to handle dismiss event if needed
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        });

        // Calculate the center coordinates of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        int popupWidth = 1000; // Set the desired width of the pop-up window in pixels
        int popupHeight = ViewGroup.LayoutParams.WRAP_CONTENT; // Set the desired height of the pop-up window

        int popupX = (screenWidth - popupWidth) / 2;
        int popupY = (screenHeight - popupHeight) / 2;

        popupWindow.showAtLocation(cardImageView, Gravity.NO_GRAVITY, popupX, popupY);

        currentPopupWindow = popupWindow;

        // Set a touch listener on the root view of the activity layout
        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (popupWindow.isShowing()) {
                        updatePlayerText();
                        popupWindow.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void populatePopupWindow(View popupView, int cardIndex) {
        // Obtain a reference to the LinearLayout inside the pop-up window
        LinearLayout cardContainer = popupView.findViewById(R.id.cardContainer);

        cardContainer.removeAllViews();


        int width = (int) getResources().getDimension(R.dimen.image_width);
        int height = (int) getResources().getDimension(R.dimen.image_height);
        final List<Card> cardStrings = players.get(cardIndex).getHand();
        List<Integer> imageList = new ArrayList<>();

        ImageView backImage = new ImageView(MainActivity.this);
        int backImageResId = getResources().getIdentifier("back", "drawable", getPackageName());
        backImage.setImageResource(backImageResId);
        LinearLayout.LayoutParams backLayoutParams = new LinearLayout.LayoutParams(width, height);
        backImage.setLayoutParams(backLayoutParams);
        cardContainer.addView(backImage);


        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                System.out.println(centerDeck);

                if (centerDeck.size() == 0) {
                    // Check if the skip button is already added
                    if (cardContainer.getChildAt(1) instanceof Button) {
                        showToast("Press Skip, To Skip Your Turn");
                    } else {
                        // Add a skip button at the beginning of cardContainer
                        Button skipButton = new Button(MainActivity.this);
                        skipButton.setText("Skip");

                        // Set the desired width and height for the skipButton
                        int buttonWidth = (int) getResources().getDimension(R.dimen.image_width);
                        int buttonHeight = (int) getResources().getDimension(R.dimen.image_height);

                        LinearLayout.LayoutParams skipButtonLayoutParams = new LinearLayout.LayoutParams(buttonWidth, buttonHeight);
                        skipButton.setLayoutParams(skipButtonLayoutParams);

                        skipButton.setLayoutParams(skipButtonLayoutParams);
                        skipButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Implement the skip functionality here
                                // For example, you can just dismiss the popup window
                                if(leadCard.getRank() == Rank.BLANK && leadCard.getSuit() == Suit.BLANK) {
                                    showToast("1st Player MUST play card");
                                    return;
                                }

                                Card blankCard = new Card(Suit.BLANK, Rank.BLANK);
                                centerCards.put(currentPlayerIndex, blankCard);
                                System.out.println(centerCards.size());

                                currentPlayerIndex = (currentPlayerIndex + 1) % 4; // Pass the turn to the next player
                                highlightPlayer(currentPlayerIndex);
                                checkWinTrick();
                                popupWindow.dismiss();
                            }
                        });
                        cardContainer.addView(skipButton, 1); // Add the skip button at index 0
                    }

                    return;
                }
                cardStrings.add(0, centerDeck.get(0));
                // Remove the card from the center deck
                centerDeck.remove(0);

                // After adding the card, recreate the popup window with the updated card data
                populatePopupWindow(popupView, activeCard);

            }
        });

//        for (Map.Entry<Integer, Card> entry : centerCards.entrySet()) {
//            int key = entry.getKey();
//            Card value = entry.getValue();
//            System.out.println("Key: " + key + ", Value: " + value);
//        }


        // Add card images dynamically to the LinearLayout
        for (Card card : cardStrings) {
            String cardString = card.toString();
            int imageResId = getResources().getIdentifier(cardString, "drawable", getPackageName());
            imageList.add(imageResId);
        }

        for (int i = 0; i < imageList.size(); i++) {
            final Card card = cardStrings.get(i); // Get the corresponding card string

            ImageView imageView = new ImageView(MainActivity.this);


            imageView.setImageResource(imageList.get(i));

            // Set the desired image size

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            int paddingBetweenImage = (int) (10 * Resources.getSystem().getDisplayMetrics().density);
            layoutParams.setMargins(paddingBetweenImage, 0, paddingBetweenImage, 0 );
            imageView.setLayoutParams(layoutParams);

            AlphaAnimation alphaAnimation;
            if (trickNumber != 1 && centerCards.size() == 0) {
                alphaAnimation = new AlphaAnimation(1.0f, 1.0f); // Set opacity to 100%
            } else if (card.getRank() != leadCard.getRank() && card.getSuit() != leadCard.getSuit()) {
                alphaAnimation = new AlphaAnimation(0.5f, 0.5f); // Set opacity to 50%
            } else {
                alphaAnimation = new AlphaAnimation(1.0f, 1.0f); // Set opacity to 100%
            }

            alphaAnimation.setDuration(0); // Duration of the animation (in milliseconds)
            alphaAnimation.setFillAfter(true); // Retain the final state of the animation
            imageView.startAnimation(alphaAnimation); // Apply the animation to the ImageView

            // Set an OnClickListener for the ImageView
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle the click event
                    System.out.println("Clicked on card: " + card.toString());

                    if (trickNumber != 1 && centerCards.size() == 0) {
                        leadCard = card;
                        centerCards.put(currentPlayerIndex, card);
                    } else if (card.getRank() != leadCard.getRank() && card.getSuit() != leadCard.getSuit()) {
                        showToast("Unavailable card!");
                        return;
                    } else {
                        centerCards.put(currentPlayerIndex, card);
                    }



                    Player player = players.get(cardIndex);
                    player.removeFromHand(card);
                    currentPlayerIndex = (currentPlayerIndex + 1) % 4; // Pass the turn to the next player
                    highlightPlayer(currentPlayerIndex);


                    // Set imageView to visible
                    cardImgOpenList.get(activeCard).setVisibility(View.VISIBLE);

                    // Update the image resource of imageViewTopBack based on the clicked card
                    int cardImageResId = getResources().getIdentifier(card.toString(), "drawable", getPackageName());
                    cardImgOpenList.get(activeCard).setImageResource(cardImageResId);
                    updatePlayerText();
                    checkWinTrick();
                    checkRoundFinish();
                    popupWindow.dismiss();
                }
            });

            cardContainer.addView(imageView);


        }

    }

    private void checkWinTrick() {
        if(centerCards.size() == 4) {
            if (centerCards.get(0).getSuit() == Suit.BLANK && centerCards.get(1).getSuit() == Suit.BLANK
                    && centerCards.get(2).getSuit() == Suit.BLANK && centerCards.get(3).getSuit() == Suit.BLANK) {
                showToast("Please Play According To Rule");
                centerCards.clear();
            } else {
                int largestCardValue = Integer.MIN_VALUE;
                int winnerIndex = -1;
                for (Map.Entry<Integer, Card> entry : centerCards.entrySet()) {
                    int currentPoint = entry.getValue().getRank().getValue();
                    if (currentPoint > largestCardValue) {
                        largestCardValue = currentPoint;
                        winnerIndex = entry.getKey();
                    }
                }
                for(ImageView card: cardImgOpenList) {
                    card.setVisibility(View.GONE);
                }
                System.out.println("\nGAME LOG: Winner is " + (winnerIndex + 1));
                showWinnerDialog(this,"Trick " + trickNumber
                        +": Player " + (winnerIndex+1) +" is the winner!");
                centerCards.clear();
                nextTrick(winnerIndex);
                highlightPlayer(winnerIndex);
            }

        }
    }

    public void nextTrick(int winnerIndex) {
        trickNumber++;
        updatePlayerText();
        if(trickNumber != 1) {
//            centerLeadCard.setVisibility(View.INVISIBLE);
            int blankImageResId = getResources().getIdentifier("back", "drawable", getPackageName());
            centerLeadCard.setImageResource(blankImageResId);
        }
        leadCard = new Card(Suit.BLANK, Rank.BLANK);
        currentPlayerIndex = winnerIndex;

    }

    private void calculateScore() {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int score = player.getScore();
            if (player.getHand().size() == 0) {
                score += 0;
            } else {
                for (Card card : player.getHand()) {
                    System.out.print(card.getRank().getValue() + " + ");
                    score += card.getRank().getValue();
                }
            }
            player.setScore(score);
            updatePlayerText();
            System.out.println(player.getName() + ": " + player.getScore());
        }
    }

    private void updatePlayerText() {
        for(int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            textViewPlayerList.get(i).setText(player.getName() + "\nScore: " + player.getScore() + "\nCard(s): " + player.getHand().size());
        }
        textViewLeadCard.setText("LeadCard\nTrick: "+ trickNumber + "\nRound: " + round + "\nCard Left: " + centerDeck.size());
    }

    private void checkRoundFinish() {
        for(Player player: players) {
            if (player.getScore() >= 100) {
                System.out.println("Game Over");

                showWinnerDialog(this, "Game Finish! " + "Player " + checkFinalWinner() + " win the game!!!\nClick OK to restart the game.");
                restartGame();

            }
            if(player.getHand().size() == 0) {
                calculateScore();
                showWinnerDialog(this, "Round " + round + " finish. Starting next round...");
                nextRound();
            }
        }
    }

    private void nextRound() {
        round++;
        trickNumber = 1;
        deck.clear();
        centerCards.clear();
        centerDeck.clear();

        deck = createDeck();
        Collections.shuffle(deck);
        for (Player player : players) {
            player.getHand().clear();
        }
        // Initialize the center cards list
        centerCards = new LinkedHashMap<>();
        centerDeck = new ArrayList<>();


        // Deal cards to each player
        dealCards();

        currentPlayerIndex = determineFirstPlayerIndex();

        centerLeadCard.setVisibility(View.VISIBLE);
        int imageResId = getResources().getIdentifier(leadCard.toString(), "drawable", getPackageName());
        centerLeadCard.setImageResource(imageResId);

        // Hide the open cards images
        for (ImageView cardOpen : cardImgOpenList) {
            cardOpen.setVisibility(View.GONE);
        }

        // Update the player scores on the screen
        updatePlayerText();

        highlightPlayer(currentPlayerIndex);
    }

    private int checkFinalWinner() {
        int smallestScore = 1000;
        int winnerIndex = 0;
        for (Player player : players) {
            if (player.getScore() < smallestScore) {
                winnerIndex = players.indexOf(player);
                smallestScore = player.getScore();
            }
        }
        return winnerIndex;
    }

    private void restartGame() {
        // Reset game variables and data
        trickNumber = 1;
        round = 1;
        leadCard = new Card(Suit.BLANK, Rank.BLANK);
        currentPlayerIndex = determineFirstPlayerIndex();
        centerCards.clear();
        centerDeck.clear();

        // Clear player hands and reset scores
        for (Player player : players) {
            player.clearHand();
            player.setScore(0);
        }

        // Deal cards again
        dealCards();

        // Reset center card image and visibility
        centerLeadCard.setVisibility(View.VISIBLE);
        int imageResId = getResources().getIdentifier(leadCard.toString(), "drawable", getPackageName());
        centerLeadCard.setImageResource(imageResId);

        // Hide the open cards images
        for (ImageView cardOpen : cardImgOpenList) {
            cardOpen.setVisibility(View.GONE);
        }

        // Update the player scores on the screen
//        calculateScore();
        updatePlayerText();


        // Highlight the first player
        currentPlayerIndex = determineFirstPlayerIndex();
        highlightPlayer(currentPlayerIndex);
    }



    private void showWinnerDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Congratulations!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void highlightPlayer(int playerNumber) {
        // Reset all text colors to black
        textViewPlayer1.setTextColor(getResources().getColor(android.R.color.black));
        textViewPlayer2.setTextColor(getResources().getColor(android.R.color.black));
        textViewPlayer3.setTextColor(getResources().getColor(android.R.color.black));
        textViewPlayer4.setTextColor(getResources().getColor(android.R.color.black));

        // Set the current player's text color to yellow
        switch (playerNumber) {
            case 0:
                textViewPlayer1.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                break;
            case 1:
                textViewPlayer2.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                break;
            case 2:
                textViewPlayer3.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                break;
            case 3:
                textViewPlayer4.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                break;
            default:
                break;
        }
    }
}
