/*
 * Copyright (c) 2018-2021 Taner Sener
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.arthenica.ffmpegkit.x265encoder;

import static com.arthenica.ffmpegkit.x265encoder.MainActivity.TAG;
import static com.arthenica.ffmpegkit.x265encoder.MainActivity.notNull;

import android.os.Bundle;
//import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegKitConfig;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.LogCallback;
import com.arthenica.ffmpegkit.LogRedirectionStrategy;
import com.arthenica.ffmpegkit.ReturnCode;
import com.arthenica.ffmpegkit.util.DialogUtil;
import com.arthenica.ffmpegkit.util.ResourcesUtil;
import com.arthenica.smartexception.java.Exceptions;
import com.arthenica.ffmpegkit.SessionState;

import java.io.File;
import java.io.IOException;


public class EncoderTabFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private EditText mp4inputText;
    private CheckBox checkboxOverwrite;
    private TextView outputLabel;
    private String selectedEncodeAction;
    private TextView outputText;
    private AlertDialog stabilizeProgressDialog;

    public EncoderTabFragment() {
        super(R.layout.fragment_encoder_tab);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mp4inputText = view.findViewById(R.id.mp4inputText);
        checkboxOverwrite = view.findViewById(R.id.checkbox_overwrite);
        //mp4outputText = view.findViewById(R.id.mp4outputText);
        outputLabel = view.findViewById(R.id.outputLabel);

        Spinner encodeActionSpinner = view.findViewById(R.id.encodeActionSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.encode_action, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        encodeActionSpinner.setAdapter(adapter);
        encodeActionSpinner.setOnItemSelectedListener(this);

        View runFFmpegButton = view.findViewById(R.id.runFFmpegButton);
        runFFmpegButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String encodeAction = getSelectedEncodeAction();
                if ( encodeAction != "stabilize") {
                    runFFmpeg();
                } else {
                    stabilizeVideo();
                }
            }
        });

        selectedEncodeAction = getResources().getStringArray(R.array.encode_action)[0];
        outputText = view.findViewById(R.id.outputText);
        outputText.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onResume() {
        super.onResume();
        setActive();
    }


    public static EncoderTabFragment newInstance() {
        return new EncoderTabFragment();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedEncodeAction = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // DO NOTHING
    }


    // Start run of runFFmpeg for everything except stabilize
    public void runFFmpeg() {
        clearOutput();

        String encodeAction = getSelectedEncodeAction();
        String actionString = "";
        String fileNameAddString = "";
        switch  (encodeAction) {
            case "convert 4k2k":
                actionString = " -vf scale=1920:1080 ";
                fileNameAddString = "-1080p.mp4";
                break;
            case "crop 4k2k":
                actionString = " -vf crop=1920:1080 ";
                fileNameAddString = "-crop-1080p.mp4";
                break;
            case "convert 4k720p":
                actionString = " -vf scale=1280:720 ";
                fileNameAddString = "-720p.mp4";
                break;
            case "crop 4k720p":
                actionString = " -vf crop=1280:720 ";
                fileNameAddString = "-crop-720p.mp4";
                break;
            case "convert 4k2k portrait":
                actionString = " -vf scale=1080:1920 ";
                fileNameAddString = "-1080p.mp4";
                break;
            case "crop 4k2k portrait":
                actionString = " -vf crop=1080:1920 ";
                fileNameAddString = "-crop-1080p.mp4";
                break;
            case "convert 4k720p portrait":
                actionString = " -vf scale=720:1280 ";
                fileNameAddString = "-720p.mp4";
                break;
            case "crop 4k720p portrait":
                actionString = " -vf crop=720:1280 ";
                fileNameAddString = "-crop-720p.mp4";
                break;
            /* case "stabilize":
                actionString = "stabilize";
                fileNameAddString = "-stab.mp4";
                break; */
            case "deshake":
                //ffmpeg -i input.mov -vf deshake output.mov
                actionString = " -vf deshake ";
                fileNameAddString = "-deshake.mp4";
                break;

        }

        String fileName = mp4inputText.getText().toString();
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        StringBuilder buildCommand = new StringBuilder();
        if ( checkboxOverwrite.isChecked()) {
            buildCommand.append(" -y ");
        }
        buildCommand.append("-i /storage/emulated/0/DCIM/Camera/");
        buildCommand.append(String.format("%s", mp4inputText.getText().toString()));
        if ( actionString != "-vf deshake") {
            buildCommand.append(" -c:v libx265 -c:a copy -threads 3 -crf 25 ");
        }
        buildCommand.append(actionString); 
        if ( actionString != "-vf deshake") {
            buildCommand.append(" -preset medium -movflags faststart -movflags use_metadata_tags -f mp4 ");
        } else {
            buildCommand.append(" -movflags use_metadata_tags ");
        }
        buildCommand.append(" /storage/emulated/0/DCIM/Camera/");
        buildCommand.append(baseName);
        buildCommand.append(fileNameAddString);

        final String ffmpegCommand = buildCommand.toString();

        outputLabel.setText("Output: ");
        outputLabel.append(baseName);
        outputLabel.append(fileNameAddString);

        android.util.Log.d(MainActivity.TAG, String.format("Current log level is %s.", FFmpegKitConfig.getLogLevel()));

        android.util.Log.d(MainActivity.TAG, "Testing FFmpeg COMMAND asynchronously.");

        android.util.Log.d(MainActivity.TAG, String.format("FFmpeg process started with arguments: '%s'", ffmpegCommand));

        FFmpegKit.executeAsync(ffmpegCommand, new FFmpegSessionCompleteCallback() {

            @Override
            public void apply(final FFmpegSession session) {
                final SessionState state = session.getState();
                final ReturnCode returnCode = session.getReturnCode();

                android.util.Log.d(MainActivity.TAG, String.format("FFmpeg process exited with state %s and rc %s.%s", FFmpegKitConfig.sessionStateToString(state), returnCode, notNull(session.getFailStackTrace(), "\n")));

                if (state == SessionState.FAILED || !returnCode.isValueSuccess()) {
                    MainActivity.addUIAction(new Runnable() {

                        @Override
                        public void run() {
                            Popup.show(requireContext(), "Command failed. Please check output for the details.");
                        }
                    });
                }
            }
        }, new LogCallback() {

            @Override
            public void apply(final com.arthenica.ffmpegkit.Log log) {
                MainActivity.addUIAction(new Runnable() {

                    @Override
                    public void run() {
                        appendOutput(log.getMessage());
                    }
                });

                throw new AndroidRuntimeException("I am test exception thrown by the application");
            }
        }, null);
    }
    // End of run of runFFmpeg for everything except stabilize




    // Start of stabilize video
    public void stabilizeVideo() {
        final File shakeResultsFile = getShakeResultsFile();

        clearOutput();

        String fileName = String.format("%s", mp4inputText.getText().toString());
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        String videoFilePath = "/storage/emulated/0/DCIM/Camera/" + fileName;
        String stabilizedVideoFile = "/storage/emulated/0/DCIM/Camera/" + baseName + "-stab.mp4";

        if (shakeResultsFile.exists()) {
            shakeResultsFile.delete();
        }

        outputLabel.setText("Output: ");
        outputLabel.append(baseName);
        outputLabel.append("-stab.mp4");

        // Start analysis
        final String analyzeVideoCommand = String.format("-y -i %s -vf vidstabdetect=shakiness=10:accuracy=15:result=%s -f null -", videoFilePath, shakeResultsFile.getAbsolutePath());

        showStabilizeProgressDialog();

        Log.d(TAG, String.format("FFmpeg process started with arguments: '%s'.", analyzeVideoCommand));

        FFmpegKit.executeAsync(analyzeVideoCommand, new FFmpegSessionCompleteCallback() {


            @Override
            public void apply(final FFmpegSession secondSession) {
                Log.d(TAG, String.format("FFmpeg process exited with state %s and rc %s.%s", secondSession.getState(), secondSession.getReturnCode(), notNull(secondSession.getFailStackTrace(), "\n")));

                if (ReturnCode.isSuccess(secondSession.getReturnCode())) {
                    final String stabilizeVideoCommand = String.format("-y -i %s -vf vidstabtransform=smoothing=30:input=%s -c:v mpeg4 %s", videoFilePath, shakeResultsFile.getAbsolutePath(), stabilizedVideoFile);

                    Log.d(TAG, String.format("FFmpeg process started with arguments: '%s'.", stabilizeVideoCommand));

                    FFmpegKit.executeAsync(stabilizeVideoCommand, new FFmpegSessionCompleteCallback() {

                        @Override
                        public void apply(final FFmpegSession thirdSession) {
                            Log.d(TAG, String.format("FFmpeg process exited with state %s and rc %s.%s", thirdSession.getState(), thirdSession.getReturnCode(), notNull(thirdSession.getFailStackTrace(), "\n")));

                            hideStabilizeProgressDialog();

                            MainActivity.addUIAction(new Runnable() {

                                @Override
                                public void run() {
                                    if (ReturnCode.isSuccess(thirdSession.getReturnCode())) {
                                        Log.d(TAG, "Stabilize video completed successfully.");

                                    } else {
                                        Popup.show(requireContext(), "Stabilize video failed. Please check logs for the details.");
                                    }
                                }
                            });
                        }
                    });

                } else {
                    MainActivity.addUIAction(() -> {
                        hideStabilizeProgressDialog();
                        Popup.show(requireContext(), "Stabilize video failed. Please check logs for the details.");
                    });
                }
            }
        });

    }



    public String getSelectedEncodeAction() {
        String encodeAction = selectedEncodeAction;

        return encodeAction;
    }

    private void setActive() {
        Log.i(MainActivity.TAG, "Command Tab Activated");
        FFmpegKitConfig.enableLogCallback(null);
        Popup.show(requireContext(), getString(R.string.command_test_tooltip_text));
    }

    public void appendOutput(final String logMessage) {
        outputText.append(logMessage);
    }

    public void clearOutput() {
        outputText.setText("");
    }

    public File getShakeResultsFile() {
        return new File(requireContext().getCacheDir(), "transforms.trf");
    }

/*    protected void showCreateProgressDialog() {
        createProgressDialog.show();
    }

    protected void hideCreateProgressDialog() {
        createProgressDialog.dismiss();
    }
*/

    protected void showStabilizeProgressDialog() {
        stabilizeProgressDialog.show();
    }

    protected void hideStabilizeProgressDialog() {
        stabilizeProgressDialog.dismiss();
    }

}
