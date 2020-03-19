using Microsoft.EntityFrameworkCore.Migrations;

namespace SurveyServer.Data.Migrations
{
    public partial class Initial : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Survey",
                columns: table => new
                {
                    SurveyID = table.Column<string>(nullable: false),
                    SurveyTitle = table.Column<string>(maxLength: 20, nullable: false),
                    PatternLock = table.Column<string>(maxLength: 9, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Survey", x => x.SurveyID);
                });

            migrationBuilder.CreateTable(
                name: "Question",
                columns: table => new
                {
                    QuestionKey = table.Column<string>(nullable: false),
                    QuestionNum = table.Column<int>(nullable: false),
                    QuestionDescription = table.Column<string>(maxLength: 50, nullable: false),
                    RefSurveyID = table.Column<string>(nullable: true),
                    QuestionType = table.Column<int>(nullable: false),
                    Options = table.Column<string>(maxLength: 450, nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Question", x => x.QuestionKey);
                    table.ForeignKey(
                        name: "FK_Question_Survey_RefSurveyID",
                        column: x => x.RefSurveyID,
                        principalTable: "Survey",
                        principalColumn: "SurveyID",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateTable(
                name: "Response",
                columns: table => new
                {
                    ResponseID = table.Column<string>(nullable: false),
                    TimeStamp = table.Column<long>(nullable: false),
                    Longitude = table.Column<long>(nullable: false),
                    Latitude = table.Column<long>(nullable: false),
                    IMEI = table.Column<string>(maxLength: 50, nullable: true),
                    RefSurveyID = table.Column<string>(nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Response", x => x.ResponseID);
                    table.ForeignKey(
                        name: "FK_Response_Survey_RefSurveyID",
                        column: x => x.RefSurveyID,
                        principalTable: "Survey",
                        principalColumn: "SurveyID",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateTable(
                name: "Answer",
                columns: table => new
                {
                    AnswerKey = table.Column<string>(nullable: false),
                    QuestionNum = table.Column<int>(nullable: false),
                    Content = table.Column<string>(nullable: true),
                    RefResponseID = table.Column<string>(nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Answer", x => x.AnswerKey);
                    table.ForeignKey(
                        name: "FK_Answer_Response_RefResponseID",
                        column: x => x.RefResponseID,
                        principalTable: "Response",
                        principalColumn: "ResponseID",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Answer_RefResponseID",
                table: "Answer",
                column: "RefResponseID");

            migrationBuilder.CreateIndex(
                name: "IX_Question_RefSurveyID",
                table: "Question",
                column: "RefSurveyID");

            migrationBuilder.CreateIndex(
                name: "IX_Response_RefSurveyID",
                table: "Response",
                column: "RefSurveyID");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Answer");

            migrationBuilder.DropTable(
                name: "Question");

            migrationBuilder.DropTable(
                name: "Response");

            migrationBuilder.DropTable(
                name: "Survey");
        }
    }
}
