package ca.on.oicr.pde.facematcher;

/*
 *  Container class for holding score and user name for one game
 */

public class Score {
		String name;
		int score;

		public String getName() {
			return name;
		}

		public int getScore() {
			return score;
		}

		public Score(String n, int s) {
			this.score = s;
			this.name = n;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.getName()).append(TopScoreAdapter.delimiter)
					.append(this.getScore());
			return sb.toString();
		}

		public Score(String delimeted, String delimeter) {
			String[] chunks = { "", "" };
			chunks = delimeted.split(delimeter);
			if (!chunks[0].isEmpty() && !chunks[1].isEmpty()) {
				this.name = chunks[0];
				this.score = Integer.valueOf(chunks[1]);
			}
		}

	}

