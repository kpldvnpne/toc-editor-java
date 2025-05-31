record TocItem(String label, int pageNum, TocItem[] children) {
  static TocItem outline = new TocItem("", 0, new TocItem[] {
    new TocItem(
        "Cover",
        1,
        null
    ),
    new TocItem(
        "Contents",
        5,
        null
    ),
    new TocItem(
        "Introduction",
        6, // TODO: Fix this issue
        null
    ),
    new TocItem(
        "Test 1",
        12,
        new TocItem[] {
            new TocItem(
                "Listening",
                12,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        12,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        13,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        15,
                        null
                    ),
                    new TocItem(
                        "Part 4",
                        17,
                        null
                    ),
                }
            ),
            new TocItem(
                "Reading",
                18,
                new TocItem[] {
                    new TocItem(
                        "Passage 1",
                        18,
                        null
                    ),
                    new TocItem(
                        "Passage 2",
                        22,
                        null
                    ),
                    new TocItem(
                        "Passage 3",
                        26,
                        null
                    ),
                }
            ),
            new TocItem(
                "Writing",
                30,
                new TocItem[] {
                    new TocItem(
                        "Task 1",
                        30,
                        null
                    ),
                    new TocItem(
                        "Task 2",
                        31,
                        null
                    ),
                }
            ),
            new TocItem(
                "Speaking",
                32,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        32,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        32,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        32,
                        null
                    ),
                }
            ),
        }
    ),
    new TocItem(
        "Test 2",
        33,
        new TocItem[] {
            new TocItem(
                "Listening",
                33,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        33,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        34,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        36,
                        null
                    ),
                    new TocItem(
                        "Part 4",
                        38,
                        null
                    ),
                }
            ),
            new TocItem(
                "Reading",
                39,
                new TocItem[] {
                    new TocItem(
                        "Passage 1",
                        39,
                        null
                    ),
                    new TocItem(
                        "Passage 2",
                        42,
                        null
                    ),
                    new TocItem(
                        "Passage 3",
                        46,
                        null
                    ),
                }
            ),
            new TocItem(
                "Writing",
                51,
                new TocItem[] {
                    new TocItem(
                        "Task 1",
                        51,
                        null
                    ),
                    new TocItem(
                        "Task 2",
                        52,
                        null
                    ),
                }
            ),
            new TocItem(
                "Speaking",
                53,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        53,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        53,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        53,
                        null
                    ),
                }
            ),
        }
    ),
    new TocItem(
        "Test 3",
        54,
        new TocItem[] {
            new TocItem(
                "Listening",
                54,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        54,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        56,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        58,
                        null
                    ),
                    new TocItem(
                        "Part 4",
                        59,
                        null
                    ),
                }
            ),
            new TocItem(
                "Reading",
                60,
                new TocItem[] {
                    new TocItem(
                        "Passage 1",
                        60,
                        null
                    ),
                    new TocItem(
                        "Passage 2",
                        64,
                        null
                    ),
                    new TocItem(
                        "Passage 3",
                        68,
                        null
                    ),
                }
            ),
            new TocItem(
                "Writing",
                73,
                new TocItem[] {
                    new TocItem(
                        "Task 1",
                        73,
                        null
                    ),
                    new TocItem(
                        "Task 2",
                        74,
                        null
                    ),
                }
            ),
            new TocItem(
                "Speaking",
                75,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        75,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        75,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        75,
                        null
                    ),
                }
            ),
        }
    ),
    new TocItem(
        "Test 4",
        76,
        new TocItem[] {
            new TocItem(
                "Listening",
                76,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        76,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        77,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        79,
                        null
                    ),
                    new TocItem(
                        "Part 4",
                        81,
                        null
                    ),
                }
            ),
            new TocItem(
                "Reading",
                82,
                new TocItem[] {
                    new TocItem(
                        "Passage 1",
                        82,
                        null
                    ),
                    new TocItem(
                        "Passage 2",
                        86,
                        null
                    ),
                    new TocItem(
                        "Passage 3",
                        89,
                        null
                    ),
                }
            ),
            new TocItem(
                "Writing",
                95,
                new TocItem[] {
                    new TocItem(
                        "Task 1",
                        95,
                        null
                    ),
                    new TocItem(
                        "Task 2",
                        96,
                        null
                    ),
                }
            ),
            new TocItem(
                "Speaking",
                97,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        97,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        97,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        97,
                        null
                    ),
                }
            ),
        }
    ),
    new TocItem(
        "Audioscripts",
        98,
        new TocItem[] {
            new TocItem(
                "Test 1",
                98,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        98,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        99,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        100,
                        null
                    ),
                    new TocItem(
                        "Part 4",
                        101,
                        null
                    ),
                }
            ),
            new TocItem(
                "Test 2",
                103,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        103,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        104,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        105,
                        null
                    ),
                    new TocItem(
                        "Part 4",
                        107,
                        null
                    ),
                }
            ),
            new TocItem(
                "Test 3",
                109,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        109,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        110,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        111,
                        null
                    ),
                    new TocItem(
                        "Part 4",
                        113,
                        null
                    ),
                }
            ),
            new TocItem(
                "Test 4",
                115,
                new TocItem[] {
                    new TocItem(
                        "Part 1",
                        115,
                        null
                    ),
                    new TocItem(
                        "Part 2",
                        116,
                        null
                    ),
                    new TocItem(
                        "Part 3",
                        117,
                        null
                    ),
                    new TocItem(
                        "Part 4",
                        119,
                        null
                    ),
                }
            ),
        }
    ),
    new TocItem(
        "Listening and Reading answer keys",
        121,
        new TocItem[] {
            new TocItem(
                "Test 1",
                121,
                new TocItem[] {
                    new TocItem(
                        "Listening",
                        121,
                        null
                    ),
                    new TocItem(
                        "Reading",
                        122,
                        null
                    ),
                }
            ),
            new TocItem(
                "Test 2",
                123,
                new TocItem[] {
                    new TocItem(
                        "Listening",
                        123,
                        null
                    ),
                    new TocItem(
                        "Reading",
                        124,
                        null
                    ),
                }
            ),
            new TocItem(
                "Test 3",
                125,
                new TocItem[] {
                    new TocItem(
                        "Listening",
                        125,
                        null
                    ),
                    new TocItem(
                        "Reading",
                        126,
                        null
                    ),
                }
            ),
            new TocItem(
                "Test 4",
                127,
                new TocItem[] {
                    new TocItem(
                        "Listening",
                        127,
                        null
                    ),
                    new TocItem(
                        "Reading",
                        128,
                        null
                    ),
                }
            ),
        }
    ),
    new TocItem(
        "Sample Writing answers",
        129,
        new TocItem[] {
            new TocItem(
                "Test 1, Writing Task 1",
                129,
                null
            ),
            new TocItem(
                "Test 1, Writing Task 2",
                130,
                null
            ),
            new TocItem(
                "Test 2, Writing Task 1",
                131,
                null
            ),
            new TocItem(
                "Test 2, Writing Task 2",
                133,
                null
            ),
            new TocItem(
                "Test 3, Writing Task 1",
                135,
                null
            ),
            new TocItem(
                "Test 3, Writing Task 2",
                136,
                null
            ),
            new TocItem(
                "Test 4, Writing Task 1",
                138,
                null
            ),
            new TocItem(
                "Test 4, Writing Task 2",
                139,
                null
            ),
        }
    ),
    new TocItem(
        "Sample answer sheets",
        140,
        null
    ),
    new TocItem(
        "Acknowledgements",
        144,
        null
    ),
    new TocItem(
        "Cover",
        147,
        null
    ),
  });
}

