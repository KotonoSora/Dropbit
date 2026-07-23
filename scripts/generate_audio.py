import wave
import struct
import math
import os

SAMPLE_RATE = 44100

def generate_tone(frequency, duration, volume=0.5):
    num_samples = int(SAMPLE_RATE * duration)
    samples = []
    for i in range(num_samples):
        # Sine wave with simple linear envelope to avoid clicks
        envelope = 1.0
        if i < 100:
            envelope = i / 100
        elif i > num_samples - 100:
            envelope = (num_samples - i) / 100

        value = int(volume * 32767.0 * math.sin(2.0 * math.pi * frequency * i / SAMPLE_RATE) * envelope)
        samples.append(value)
    return samples

def save_wav(filename, samples):
    path = os.path.join("app/src/main/res/raw", filename)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with wave.open(path, 'w') as f:
        f.setnchannels(1)
        f.setsampwidth(2)
        f.setframerate(SAMPLE_RATE)
        for s in samples:
            f.writeframesraw(struct.pack('<h', s))
    print(f"Generated {path}")

def generate_click():
    # Short sharp sound
    save_wav("click.wav", generate_tone(880, 0.05, 0.3))

def generate_success():
    # Two rising tones
    samples = generate_tone(440, 0.1) + generate_tone(880, 0.15)
    save_wav("success.wav", samples)

def generate_error():
    # Low buzz
    samples = []
    for i in range(int(SAMPLE_RATE * 0.3)):
        value = int(0.5 * 32767.0 * math.sin(2.0 * math.pi * 110 * i / SAMPLE_RATE + 2.0 * math.sin(2.0 * math.pi * 20 * i / SAMPLE_RATE)))
        # Fade out
        envelope = max(0, 1.0 - (i / (SAMPLE_RATE * 0.3)))
        samples.append(int(value * envelope))
    save_wav("error.wav", samples)

def generate_win():
    # Upward arpeggio
    samples = (generate_tone(523.25, 0.1) + # C5
               generate_tone(659.25, 0.1) + # E5
               generate_tone(783.99, 0.1) + # G5
               generate_tone(1046.50, 0.3)) # C6
    save_wav("win.wav", samples)

def generate_lose():
    # Descending low notes
    samples = (generate_tone(220, 0.2) + # A3
               generate_tone(196, 0.2) + # G3
               generate_tone(174.61, 0.4)) # F3
    save_wav("lose.wav", samples)

def generate_milestone():
    # Bright chime
    samples = []
    num_samples = int(SAMPLE_RATE * 0.5)
    for i in range(num_samples):
        # Two frequencies for a bell-like sound
        v1 = math.sin(2.0 * math.pi * 1320 * i / SAMPLE_RATE)
        v2 = 0.5 * math.sin(2.0 * math.pi * 2640 * i / SAMPLE_RATE)
        envelope = math.exp(-6 * i / num_samples) # Exponential decay
        value = int(0.5 * 32767.0 * (v1 + v2) * envelope)
        samples.append(value)
    save_wav("milestone.wav", samples)

if __name__ == "__main__":
    generate_click()
    generate_success()
    generate_error()
    generate_win()
    generate_lose()
    generate_milestone()
